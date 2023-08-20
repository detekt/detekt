import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("packaging")
    kotlin("jvm")
    `maven-publish`
    jacoco
    `ivy-publish`
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
    maxParallelForks = if (providers.environmentVariable("CI").isPresent) {
        Runtime.getRuntime().availableProcessors()
    } else {
        (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    }
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

kotlin {
    compilerOptions {
        jvmTarget = Versions.JVM_TARGET
        progressiveMode = true
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull.toBoolean()
    }
}

testing {
    suites {
        getByName("test", JvmTestSuite::class) {
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

publishing {
    repositories {
        ivy {
            name = "gradlePluginFunctionalTest"
            url = uri(rootProject.layout.projectDirectory.dir("detekt-gradle-plugin/build/repo"))
        }
    }
    publications {
        create<IvyPublication>("ivy") {
            from(components["java"])
        }
    }
}
