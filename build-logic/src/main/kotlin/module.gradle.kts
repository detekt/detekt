import org.apache.tools.ant.taskdefs.condition.Os
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

@Suppress("MagicNumber")
listOf(8, 11, 17).forEach { jdkVersion ->
    // Windows with JDK8 are really flaky
    if (jdkVersion == 8 && Os.isFamily(Os.FAMILY_WINDOWS)) return@forEach
    // https://github.com/detekt/detekt/issues/5646
    if (project.name == "detekt-compiler-plugin") return@forEach

    val jdkTest = tasks.register<Test>("testJdk$jdkVersion") {
        val javaToolchains = project.extensions.getByType(JavaToolchainService::class)
        javaLauncher.set(
            javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(jdkVersion))
            }
        )

        description = "Runs the test suite on JDK $jdkVersion"
        group = LifecycleBasePlugin.VERIFICATION_GROUP

        // Copy inputs from normal Test task.
        val testTask = tasks.test.get()
        classpath = testTask.classpath
        testClassesDirs = testTask.testClassesDirs
    }
    tasks.check {
        dependsOn(jdkTest)
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
