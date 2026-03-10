import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.toolchain.JavaLanguageVersion
import io.qameta.allure.gradle.report.tasks.AllureReport

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


val aggregatedAllureResults by configurations.creating

dependencies {
    add(aggregatedAllureResults.name, files(layout.buildDirectory.dir("allure-results")))
}

val collectAllureResults by tasks.registering(Copy::class) {
    group = "verification"
    description = "Collects Allure results from test modules into a single root directory"

    val aggregatedResultsDir = layout.buildDirectory.dir("allure-results")
    into(aggregatedResultsDir)

    from(layout.projectDirectory.dir("test-suites/tests-smoke/build/allure-results"))
    from(layout.projectDirectory.dir("test-suites/tests-regression/build/allure-results"))
    from(layout.projectDirectory.dir("test-suites/tests-integration/build/allure-results"))

    includeEmptyDirs = false

    dependsOn(
        ":test-suites:smoke:test",
        ":test-suites:regression:test",
        ":test-suites:integration:test"
    )

    doFirst {
        delete(aggregatedResultsDir)
    }
}

tasks.named<AllureReport>("allureReport") {
    dependsOn(collectAllureResults)
    resultsDirs.set(aggregatedAllureResults)

    doFirst {
        delete(layout.buildDirectory.dir("reports/allure-report/allureReport"))
    }
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
