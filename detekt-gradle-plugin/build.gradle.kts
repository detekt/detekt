import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.util.Date

repositories {
    jcenter()
}

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.10.1"
    id("com.jfrog.bintray") version "1.8.4"
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.dokka") version "0.10.0"
    id("com.github.ben-manes.versions") version "0.27.0"
    id("io.gitlab.arturbosch.detekt") version "1.1.1"
}

group = "io.gitlab.arturbosch.detekt"
version = "1.1.1"

val spekVersion = "2.0.8"
val junitPlatformVersion = "1.5.2"
val assertjVersion = "3.13.2"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("gradle-plugin-api"))

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
    systemProperty("SPEK_TIMEOUT", 0) // disable test timeout
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
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
    configuration {
        // suppresses undocumented classes but not dokka warnings
        // https://github.com/Kotlin/dokka/issues/229 && https://github.com/Kotlin/dokka/issues/319
        reportUndocumented = false
    }
}

val generateDefaultDetektVersionFile by tasks.registering {
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

sourceSets.main.get().java.srcDir("$buildDir/generated/src")

tasks.compileKotlin {
    dependsOn(generateDefaultDetektVersionFile)
}

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn(tasks.classes)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(tasks.dokka)
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
        project.rootDir.resolve("../config/detekt/detekt.yml")
    )
}
val bintrayUser = findProperty("bintrayUser")?.toString() ?: System.getenv("BINTRAY_USER")
val bintrayKey = findProperty("bintrayKey")?.toString() ?: System.getenv("BINTRAY_API_KEY")
val detektPublication = "DetektPublication"

publishing {
    publications.create<MavenPublication>(detektPublication) {
        from(components["java"])
        artifact(sourcesJar)
        artifact(javadocJar)
        groupId = rootProject.group as? String
        artifactId = rootProject.name
        version = rootProject.version as? String
        pom {
            description.set("Static code analysis for Kotlin")
            name.set("detekt")
            url.set("https://github.com/arturbosch/detekt")
            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("Artur Bosch")
                    name.set("Artur Bosch")
                    email.set("arturbosch@gmx.de")
                }
            }
            scm {
                url.set("https://github.com/arturbosch/detekt")
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
