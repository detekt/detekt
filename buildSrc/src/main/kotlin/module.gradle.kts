import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
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

// Share sources folder with other projects for aggregated JaCoCo reports
configurations.create("transitiveSourcesElements") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("source-folders"))
    }
    sourceSets.main.get().withConvention(KotlinSourceSet::class) { kotlin }.srcDirs.forEach {
        outgoing.artifact(it)
    }
}

// Share the coverage data to be aggregated for the whole product
configurations.create("coverageDataElements") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("jacoco-coverage-data"))
    }
    // This will cause the test task to run if the coverage data is requested by the aggregation task
    outgoing.artifact(
        tasks.test.map { task ->
            task.extensions.getByType<JacocoTaskExtension>().destinationFile!!
        }
    )
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
        // Note: currently there are warnings for detekt-gradle-plugin that seemingly can't be fixed
        //       until Gradle releases an update (https://github.com/gradle/gradle/issues/16345)
        allWarningsAsErrors = when (project.name) {
            "detekt-gradle-plugin" -> false
            else -> (project.findProperty("warningsAsErrors") == "true" || System.getenv("CI") == "true")
        }
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
