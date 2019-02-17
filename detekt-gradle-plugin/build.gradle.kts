import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
        jcenter()
    }
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    jcenter()
}

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.10.0"
    id("com.jfrog.bintray") version "1.8.4"
    kotlin("jvm") version "1.3.21"
    id("org.jetbrains.dokka") version "0.9.17"
    id("com.github.ben-manes.versions") version "0.20.0"
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC13"
}

group = "io.gitlab.arturbosch.detekt"
version = "1.0.0-RC14"

val detektGradleVersion: String by project
val jcommanderVersion: String by project
val spekVersion = "2.0.0"
val junitPlatformVersion = "1.3.2"
val assertjVersion = "3.11.1"

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("reflect"))
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}

gradlePlugin {
    plugins {
        register("detektPlugin") {
            id = "io.gitlab.arturbosch.detekt"
            implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        // set options for log level LIFECYCLE
        events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_OUT
        )
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

tasks.validateTaskProperties {
    enableStricterValidation = true
}

pluginBundle {
    website = "https://arturbosch.github.io/detekt"
    vcsUrl = "https://github.com/arturbosch/detekt"
    description = "Static code analysis for Kotlin"
    tags = listOf("kotlin", "detekt", "code-analysis", "badsmells", "codesmells")

    (plugins) {
        "detektPlugin" {
            id = "io.gitlab.arturbosch.detekt"
            displayName = "Static code analysis for Kotlin"
        }
    }
}

tasks.dokka {
    // suppresses undocumented classes but not dokka warnings
    // https://github.com/Kotlin/dokka/issues/229 && https://github.com/Kotlin/dokka/issues/319
    reportUndocumented = false
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
}

val generateDefaultDetektVersionFile: Task by tasks.creating {
    val defaultDetektVersionFile =
            File("$buildDir/generated/src/io/gitlab/arturbosch/detekt", "PluginVersion.kt")

    outputs.file(defaultDetektVersionFile)

    doFirst {
        defaultDetektVersionFile.parentFile.mkdirs()
        defaultDetektVersionFile.writeText("""
            package io.gitlab.arturbosch.detekt

            internal const val DEFAULT_DETEKT_VERSION = "$version"
            """.trimIndent()
        )
    }
}

sourceSets["main"].java.srcDir("$buildDir/generated/src")

tasks.compileKotlin {
    dependsOn(generateDefaultDetektVersionFile)
}

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn("dokka")
    archiveClassifier.set("javadoc")
    from(buildDir.resolve("javadoc"))
}

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}

detekt {
    config = files(
            project.rootDir.resolve("../detekt-cli/src/main/resources/default-detekt-config.yml"),
            project.rootDir.resolve("../reports/failfast.yml")
    )
    filters = ".*/resources/.*,.*/build/.*"
}

publishing {
    publications.create<MavenPublication>("DetektPublication") {
        from(components["java"])
        artifact(sourcesJar)
        artifact(javadocJar)
        groupId = rootProject.group as? String
        artifactId = rootProject.name
        version = rootProject.version as? String
        pom.withXml {
            asNode().apply {
                appendNode("description", "Static code analysis for Kotlin")
                appendNode("name", "detekt")
                appendNode("url", "https://github.com/arturbosch/detekt")

                val license = appendNode("licenses").appendNode("license")
                license.appendNode("name", "The Apache Software License, Version 2.0")
                license.appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")
                license.appendNode("distribution", "repo")

                val developer = appendNode("developers").appendNode("developer")
                developer.appendNode("id", "Artur Bosch")
                developer.appendNode("name", "Artur Bosch")
                developer.appendNode("email", "arturbosch@gmx.de")

                appendNode("scm").appendNode("url", "https://github.com/arturbosch/detekt")
            }
        }
    }
}
