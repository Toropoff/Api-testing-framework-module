import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion

allprojects {
    group = "com.apiframework"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
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

        // Automatically pass all CLI JVM properties (-D...) from Gradle invocation
        // to the forked test JVMs.
        systemProperties(gradle.startParameter.systemPropertiesArgs)

        // Also pass selected Gradle project properties (-P...) used by the framework.
        gradle.startParameter.projectProperties
            .filterKeys { key ->
                key.startsWith("framework.") || key.startsWith("test.") || key.startsWith("auth.")
            }
            .forEach { (key, value) ->
                systemProperty(key, value)
            }

        // Keep sane default when profile is not passed from CLI.
        if (!systemProperties.containsKey("framework.profile")) {
            systemProperty("framework.profile", "dev")
        }
    }
}
