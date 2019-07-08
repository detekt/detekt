import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.util.Date

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
    id("com.gradle.plugin-publish") version "0.10.1"
    id("com.jfrog.bintray") version "1.8.4"
    kotlin("jvm") version "1.3.41"
    id("org.jetbrains.dokka") version "0.9.18"
    id("com.github.ben-manes.versions") version "0.21.0"
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC15"
}

group = "io.gitlab.arturbosch.detekt"
version = "1.0.0-RC16"

val detektGradleVersion: String by project
val jcommanderVersion: String by project
val spekVersion = "2.0.2"
val junitPlatformVersion = "1.4.1"
val assertjVersion = "3.12.2"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("gradle-plugin-api"))

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
}
val bintrayUser: String? =
    if (project.hasProperty("bintrayUser")) {
        project.property("bintrayUser").toString()
    } else {
        System.getenv("BINTRAY_USER")
    }
val bintrayKey: String? =
    if (project.hasProperty("bintrayKey")) {
        project.property("bintrayKey").toString()
    } else {
        System.getenv("BINTRAY_API_KEY")
    }
val detektPublication = "DetektPublication"

publishing {
    publications.create<MavenPublication>(detektPublication) {
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

bintray {
    user = bintrayUser
    key = bintrayKey
    val mavenCentralUser = System.getenv("MAVEN_CENTRAL_USER") ?: ""
    val mavenCentralPassword = System.getenv("MAVEN_CENTRAL_PW") ?: ""

    setPublications(detektPublication)

    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "code-analysis"
        name = "detekt"
        userOrg = "arturbosch"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/arturbosch/detekt"

        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version as? String
            released = Date().toString()

            gpg(delegateClosureOf<BintrayExtension.GpgConfig> {
                sign = true
            })

            mavenCentralSync(delegateClosureOf<BintrayExtension.MavenCentralSyncConfig> {
                sync = true
                user = mavenCentralUser
                password = mavenCentralPassword
                close = "1"
            })
        })
    })
}
