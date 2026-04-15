import org.gradle.api.plugins.JavaPluginExtension
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

val aggregatedAllureResultsDir = layout.buildDirectory.dir("allure-results")
val previousAllureHistoryDir = layout.buildDirectory.dir("reports/allure-report/allureReport/history")
val allureEnvDir = layout.projectDirectory.dir("framework-reporting/allure-results")

val suiteNames = listOf("smoke", "regression", "integration", "public-api")
val suiteAllureResultDirs = suiteNames.map { layout.projectDirectory.dir("test-suites/$it/build/allure-results") }
val allureSuiteTaskPaths = suiteNames.map { ":test-suites:$it:test" }

tasks.register("collectAllureResults") {
    group = "verification"
    description = "Collects Allure results from test modules into build/allure-results"
    mustRunAfter("runSuitesForAllure")

    doLast {
        val outputDir = aggregatedAllureResultsDir.get().asFile
        val previousHistoryDir = previousAllureHistoryDir.get().asFile

        delete(outputDir)
        outputDir.mkdirs()

        copy {
            suiteAllureResultDirs.forEach { from(it) }
            into(outputDir)
            includeEmptyDirs = false
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        // static allure config — committed resource, source: framework-reporting/src/main/resources/allure/
        copy {
            from("framework-reporting/src/main/resources/allure") { include("categories.json") }
            into(outputDir)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        // runtime-generated allure metadata — environment + executor (non-critical, may not exist on first run)
        copy {
            from(allureEnvDir) { include("environment.properties", "executor.json") }
            into(outputDir)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        if (previousHistoryDir.exists()) {
            copy {
                from(previousHistoryDir)
                into(outputDir.resolve("history"))
                includeEmptyDirs = false
            }
        }
    }
}

tasks.register("runSuitesForAllure") {
    group = "verification"
    description = "Runs smoke, regression and integration suites to prepare Allure results"

    dependsOn(allureSuiteTaskPaths)
}

// allureServe has its own aggregation pipeline that reads from each subproject's build/allure-results/
// and from allureEnvDir — it does NOT use the root build/allure-results/ populated by collectAllureResults.
// Both copies below inject into allureEnvDir so the serve pipeline picks them up, matching what allureReport sees.
tasks.named("allureServe") {
    doFirst {
        // Static defect category config — committed resource, must be present for the Categories widget.
        copy {
            from("framework-reporting/src/main/resources/allure") { include("categories.json") }
            into(allureEnvDir)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        // History from the last allureReport run — makes the Trend/History widgets consistent between
        // allureServe and allureReport. allureServe generates to a temp dir so it cannot update history;
        // history is only written by allureReport (via collectAllureResults → allureReport output).
        val historyDir = previousAllureHistoryDir.get().asFile
        if (historyDir.exists()) {
            copy {
                from(historyDir)
                into(allureEnvDir.dir("history"))
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
        }
    }
}

tasks.named("allureReport") {
    dependsOn("collectAllureResults")
    mustRunAfter("runSuitesForAllure")

    doFirst {
        delete(layout.buildDirectory.dir("reports/allure-report/allureReport"))
    }
}

tasks.register("allureReportWithTests") {
    group = "verification"
    description = "Runs all suites and then builds the aggregated Allure report"

    dependsOn("runSuitesForAllure")
    dependsOn("allureReport")
}

val forceAllureSuiteRun = gradle.startParameter.taskNames.any { taskName ->
    taskName == "allureReportWithTests" ||
        taskName.endsWith(":allureReportWithTests") ||
        taskName == "runSuitesForAllure" ||
        taskName.endsWith(":runSuitesForAllure")
}

val allureSuiteTestTasks = allureSuiteTaskPaths.toSet()

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

    // TODO: Javadoc warnings are suppressed while the framework is in active development.
    //  Remove '-Xdoclint:none' and add proper javadoc once the public API stabilizes.
    tasks.withType<Javadoc>().configureEach {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    tasks.withType<Test>().configureEach {
        useTestNG()
        doFirst {
            val weaver = configurations.findByName("testRuntimeClasspath")
                ?.files
                ?.find { it.name.contains("aspectjweaver") }
            if (weaver != null) jvmArgs("-javaagent:${weaver.absolutePath}")
        }
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

        systemProperty("allure.env.dir", allureEnvDir.asFile.absolutePath)

        val allureResultsDir = project.layout.buildDirectory.dir("allure-results")
        systemProperty("allure.results.directory", allureResultsDir.get().asFile.absolutePath)

        if (forceAllureSuiteRun && path in allureSuiteTestTasks) {
            outputs.upToDateWhen { false }
        }

        doFirst {
            delete(allureResultsDir)
        }
    }
}
