import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

plugins {
    id("packaging")
    kotlin("jvm")
    id("maven-publish")
    id("jacoco")
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
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
    val compileTestSnippets = providers.gradleProperty("compile-test-snippets").orNull.toBoolean()
    systemProperty("compile-test-snippets", compileTestSnippets)
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
}

kotlin {
    compilerOptions {
        jvmTarget = Versions.JVM_TARGET
        progressiveMode = true
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull.toBoolean()
    }
}

val java8Launcher = javaToolchains.launcherFor {
    languageVersion = JavaLanguageVersion.of(8)
}

project.tasks.withType<UsesKotlinJavaToolchain>().configureEach {
    kotlinJavaToolchain.toolchain.use(java8Launcher)
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    consistentResolution {
        useCompileClasspathVersions()
    }
}
