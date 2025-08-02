import com.gradle.develocity.agent.gradle.test.DevelocityTestConfiguration
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

plugins {
    id("packaging")
    kotlin("jvm")
    id("maven-publish")
    id("jacoco")
}

// Add attributes to JAR manifest, to be used at runtime
tasks.withType<Jar>().configureEach {
    manifest {
        attributes(mapOf("DetektVersion" to Versions.DETEKT))
        attributes(mapOf("KotlinImplementationVersion" to versionCatalog.findVersion("kotlin").get().requiredVersion))
    }
}

val versionCatalog = versionCatalogs.named("libs")

jacoco.toolVersion = versionCatalog.findVersion("jacoco").get().requiredVersion

tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
    systemProperty("junit.platform.discovery.issue.severity.critical", "INFO")
    val compileTestSnippets = providers.gradleProperty("compile-test-snippets").orNull.toBoolean()
    systemProperty("compile-test-snippets", compileTestSnippets)

    if (compileTestSnippets) {
        maxHeapSize = "3g"
    } else {
        maxHeapSize = "2g"
    }

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

val jvmTargetVersion = versionCatalog.findVersion("jvm-target").get().requiredVersion

kotlin {
    compilerOptions {
        progressiveMode = true
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull.toBoolean()
        freeCompilerArgs.add("-Xjvm-default=all")
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(jvmTargetVersion)
    }
}

val javaLauncher = javaToolchains.launcherFor {
    languageVersion = JavaLanguageVersion.of(versionCatalog.findVersion("java-compile-toolchain").get().requiredVersion)
}

project.tasks.withType<UsesKotlinJavaToolchain>().configureEach {
    kotlinJavaToolchain.toolchain.use(javaLauncher)
}

testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter(versionCatalog.findVersion("junit").get().requiredVersion)
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.toVersion(jvmTargetVersion)
    targetCompatibility = JavaVersion.toVersion(jvmTargetVersion)
    consistentResolution {
        useCompileClasspathVersions()
    }
}
