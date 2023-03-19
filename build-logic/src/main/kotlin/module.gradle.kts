import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("packaging")
    kotlin("jvm")
    `maven-publish`
    jacoco
}

// Add attributes to JAR manifest, to be used at runtime
tasks.withType<Jar>().configureEach {
    manifest {
        attributes(mapOf("DetektVersion" to Versions.DETEKT))
        attributes(mapOf("KotlinImplementationVersion" to versionCatalog.findVersion("kotlin").get().requiredVersion))
    }
}

val versionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

jacoco.toolVersion = versionCatalog.findVersion("jacoco").get().requiredVersion

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    val testJavaVersion = providers.gradleProperty("io.github.detekt.javaToolchain.test")
        .getOrElse(Versions.JVM_TARGET.toString())
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(testJavaVersion))
        }
    )
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
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-progressive")
        allWarningsAsErrors.set(providers.gradleProperty("warningsAsErrors").orNull.toBoolean())
    }
}

testing {
    suites {
        getByName("test", JvmTestSuite::class) {
            useJUnitJupiter(versionCatalog.findVersion("junit").get().requiredVersion)
        }
    }
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(Versions.JVM_TARGET))
    consistentResolution {
        useCompileClasspathVersions()
    }
}
