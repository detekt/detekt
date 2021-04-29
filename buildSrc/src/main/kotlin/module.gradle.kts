import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `maven-publish`
    jacoco
    id("detekt")
}

// bundle detekt's version for all jars to use it at runtime
tasks.withType<Jar>().configureEach {
    manifest {
        attributes(mapOf("DetektVersion" to Versions.DETEKT))
    }
}

val catalogs = project.extensions.getByType(VersionCatalogsExtension::class)

jacoco.toolVersion = catalogs.named("libs").findVersion("jacoco").get().requiredVersion

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("spek2.jvm.cg.scan.concurrency", 1) // use one thread for classpath scanning
    systemProperty("spek2.execution.test.timeout", 0) // disable test timeout
    systemProperty("spek2.discovery.parallel.enabled", 0) // disable parallel test discovery
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
        languageVersion = "1.5"
        freeCompilerArgs = listOf(
            "-progressive",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
        // Usage: <code>./gradlew build -PwarningsAsErrors=true</code>.
        allWarningsAsErrors = project.findProperty("warningsAsErrors") == "true" || System.getenv("CI") == "true"
    }
}

dependencies {
    implementation(platform(project(":detekt-bom")))
    compileOnly(kotlin("stdlib-jdk8"))

    testImplementation("org.assertj:assertj-core")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm")
    testImplementation("org.reflections:reflections")
    testImplementation("io.mockk:mockk")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5")
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    publications.named<MavenPublication>(DETEKT_PUBLICATION) {
        from(components["java"])
    }
}
