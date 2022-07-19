import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("packaging")
    kotlin("jvm")
    `maven-publish`
    jacoco
}

// bundle detekt's version for all jars to use it at runtime
tasks.withType<Jar>().configureEach {
    manifest {
        attributes(mapOf("DetektVersion" to Versions.DETEKT))
    }
}

val versionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

jacoco.toolVersion = versionCatalog.findVersion("jacoco").get().requiredVersion

tasks.withType<PublishToMavenRepository>().configureEach {
    notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/13468")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
    val compileSnippetText: Boolean = if (project.hasProperty("compile-test-snippets")) {
        (project.property("compile-test-snippets") as String).toBoolean()
    } else {
        false
    }
    systemProperty("compile-snippet-tests", compileSnippetText)
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
    kotlinOptions {
        jvmTarget = Versions.JVM_TARGET
        apiVersion = "1.4"
        freeCompilerArgs = listOf(
            "-progressive",
            "-Xsuppress-version-warnings",
        )
        // Note: Currently there are warnings for detekt-gradle-plugin that seemingly can't be fixed
        //       until Gradle releases an update (https://github.com/gradle/gradle/issues/16345)
        allWarningsAsErrors = when (project.name) {
            "detekt-gradle-plugin" -> false
            else -> project.findProperty("warningsAsErrors") == "true"
        }
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
