import com.gradle.develocity.agent.gradle.test.DevelocityTestConfiguration
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

plugins {
    id("packaging")
    kotlin("jvm")
    id("maven-publish")
    id("jacoco")
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

val jvmTargetVersion = versionCatalog.findVersion("jvm-target").get().requiredVersion
val jvmMajorVersion = jvmTargetVersion.toIntOrNull() ?: 8

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(jvmTargetVersion)
        extraWarnings = true
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull.toBoolean()
        freeCompilerArgs.add("-Xcontext-parameters")
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

// Pretend AGP API and JUnit targets JVM 8. Required while detekt itself targets JVM 8 while AGP API targets JVM 11 and JUnit 6 targets JVM 17.
dependencies {
    components {
        setOf(
            "com.android.tools.build:gradle-api",
            "org.junit.jupiter:junit-jupiter",
            "org.junit.jupiter:junit-jupiter-api",
            "org.junit.jupiter:junit-jupiter-engine",
            "org.junit.jupiter:junit-jupiter-params",
            "org.junit.platform:junit-platform-commons",
            "org.junit.platform:junit-platform-engine",
            "org.junit.platform:junit-platform-launcher",
        ).forEach {
            withModule(it) {
                allVariants {
                    attributes {
                        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, jvmMajorVersion)
                    }
                }
            }
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.toVersion(jvmTargetVersion)
    targetCompatibility = JavaVersion.toVersion(jvmTargetVersion)
    if (project.name !in setOf("detekt-gradle-plugin", "detekt-test-junit")) {
        // DGP uses different versions of kotlin-gradle-api in test runtime and compile time
        consistentResolution {
            useCompileClasspathVersions()
        }
    }
}
