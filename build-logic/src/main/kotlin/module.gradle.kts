import com.gradle.develocity.agent.gradle.test.DevelocityTestConfiguration
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode

plugins {
    id("packaging")
    kotlin("jvm")
    id("jacoco")
    id("com.gradleup.tapmoc")
}

val versionCatalog = versionCatalogs.named("libs")

jacoco.toolVersion = versionCatalog.findVersion("jacoco").get().requiredVersion

tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
    systemProperty("junit.platform.discovery.issue.severity.critical", "INFO")
    val compileTestSnippets = providers.gradleProperty("compile-test-snippets").orNull.toBoolean()
    systemProperty("compile-test-snippets", compileTestSnippets)

    maxHeapSize = "3g"

    testLogging {
        // set options for log level LIFECYCLE
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.STANDARD_ERROR,
            TestLogEvent.STANDARD_OUT,
            TestLogEvent.SKIPPED
        )
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }

    configure<JacocoTaskExtension> {
        excludes = listOf("org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors")
    }

    configure<DevelocityTestConfiguration> {
        testRetry {
            @Suppress("MagicNumber")
            if (providers.environmentVariable("CI").isPresent) {
                maxRetries = 3
                maxFailures = 20
            }
        }
        predictiveTestSelection {
            enabled = providers.gradleProperty("enablePTS").map(String::toBooleanStrict)
        }
    }
}

kotlin {
    compilerOptions {
        extraWarnings = true
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull.toBoolean()
        if (project.name != "detekt-gradle-plugin") {
            // DGP compiles with Kotlin 2.1.21. Support for the stable version of this flag was only added in 2.2.0.
            // See KT-73007 & KT-74590
            jvmDefault = JvmDefaultMode.NO_COMPATIBILITY

            // Only enable progressive mode in non-DGP modules. DGP doesn't compile with latest language version so
            // progressive mode is not appropriate.
            progressiveMode = true
        } else {
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }
}

testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter(versionCatalog.findVersion("junit").get().requiredVersion)
        }
    }
}

tapmoc {
    @Suppress("MagicNumber")
    java(17)
}

java {
    withSourcesJar()
    if (project.name !in setOf("detekt-gradle-plugin", "detekt-test-junit")) {
        // DGP uses different versions of kotlin-gradle-api in test runtime and compile time
        consistentResolution {
            useCompileClasspathVersions()
        }
    }
}
