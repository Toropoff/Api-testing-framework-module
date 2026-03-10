import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("io.qameta.allure") version "2.12.0"
}

allprojects {
    group = "com.apiframework"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

val testModules = listOf("tests-smoke", "tests-regression", "tests-integration")

val aggregateAllureResults by tasks.registering(Copy::class) {
    group = "verification"
    description = "Collects Allure results from test modules into a single directory."

    from(testModules.map { "$it/build/allure-results" })
    from(testModules.map { "$it/allure-results" })
    into(layout.buildDirectory.dir("allure-results"))

    mustRunAfter(testModules.map { ":$it:test" })
}

artifacts {
    add("allureRawResultElements", layout.buildDirectory.dir("allure-results")) {
        builtBy(aggregateAllureResults)
    }
}

tasks.named("allureReport") {
    dependsOn(aggregateAllureResults)
    doFirst {
        delete(layout.buildDirectory.dir("reports/allure-report/allureReport"))
    }
}

tasks.named("allureServe") {
    dependsOn(aggregateAllureResults)
}

subprojects {
    apply(plugin = "java-library")

    extensions.configure<JavaPluginExtension> {
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

    tasks.withType<Test>().configureEach {
        useTestNG()
        testLogging {
            events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showStackTraces = true
            showStandardStreams = false
        }

        systemProperties(gradle.startParameter.systemPropertiesArgs)

        gradle.startParameter.projectProperties
            .filterKeys { key ->
                key.startsWith("framework.") || key.startsWith("test.") || key.startsWith("auth.")
            }
            .forEach { (key, value) ->
                systemProperty(key, value)
            }

        if (!systemProperties.containsKey("framework.profile")) {
            systemProperty("framework.profile", "dev")
        }
    }
}
