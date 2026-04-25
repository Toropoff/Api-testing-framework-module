import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("io.qameta.allure") version "3.0.1"
}

allprojects {
    group = "com.apiframework"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

/* =========================
   Allure aggregation
   ========================= */

val aggregatedAllureResultsDir = layout.buildDirectory.dir("allure-results")
val previousAllureHistoryDir = layout.buildDirectory.dir("reports/allure-report/allureReport/history")
val allureEnvDir = layout.projectDirectory.dir("framework-reporting/allure-results")

val suiteNames = listOf("smoke", "regression", "integration", "public-api")
val suiteAllureResultDirs = suiteNames.map { layout.projectDirectory.dir("test-suites/$it/build/allure-results") }
val allureSuiteTaskPaths = suiteNames.map { ":test-suites:$it:test" }

tasks.register("runSuitesForAllure") {
    group = "verification"
    dependsOn(allureSuiteTaskPaths)
}

tasks.register("collectAllureResults") {
    group = "verification"
    mustRunAfter("runSuitesForAllure")

    doLast {
        val outputDir = aggregatedAllureResultsDir.get().asFile
        val previousHistoryDir = previousAllureHistoryDir.get().asFile

        delete(outputDir)
        outputDir.mkdirs()

        copy {
            suiteAllureResultDirs.forEach { from(it) }
            into(outputDir)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        copy {
            from("framework-reporting/src/main/resources/allure") { include("categories.json") }
            into(outputDir)
        }

        copy {
            from(allureEnvDir) { include("environment.properties", "executor.json") }
            into(outputDir)
        }

        if (previousHistoryDir.exists()) {
            copy {
                from(previousHistoryDir)
                into(outputDir.resolve("history"))
            }
        }
    }
}

tasks.named("allureReport") {
    dependsOn("collectAllureResults")
    mustRunAfter("runSuitesForAllure")
}

tasks.register("allureReportWithTests") {
    group = "verification"
    dependsOn("runSuitesForAllure", "allureReport")
}

/* =========================
   Java config (SAFE)
   ========================= */

subprojects {

    plugins.withId("java") {

        extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
            withJavadocJar()
            withSourcesJar()
        }

        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release.set(21)
        }

        tasks.withType<Javadoc>().configureEach {
            (options as StandardJavadocDocletOptions)
                .addStringOption("Xdoclint:none", "-quiet")
        }

        tasks.withType<Test>().configureEach {
            useTestNG()

            // AspectJ (если есть)
            doFirst {
                val weaver = configurations.findByName("testRuntimeClasspath")
                    ?.files
                    ?.find { it.name.contains("aspectjweaver") }

                if (weaver != null) {
                    jvmArgs("-javaagent:${weaver.absolutePath}")
                }
            }

            testLogging {
                events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
                exceptionFormat = TestExceptionFormat.FULL
                showExceptions = true
                showStackTraces = true
            }

            val allureResultsDir = project.layout.buildDirectory.dir("allure-results")
            systemProperty("allure.results.directory", allureResultsDir.get().asFile.absolutePath)

            doFirst {
                delete(allureResultsDir)
            }
        }
    }
}