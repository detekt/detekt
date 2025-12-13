plugins {
    `java-library`
    `maven-publish`
    signing
}

publishing {
    repositories {
        maven {
            name = "mavenCentral"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = "SONATYPE_USERNAME".byProperty
                password = "SONATYPE_PASSWORD".byProperty
            }
        }
        maven {
            name = "sonatypeSnapshot"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            credentials {
                username = "SONATYPE_USERNAME".byProperty
                password = "SONATYPE_PASSWORD".byProperty
            }
        }
    }
    // We don't need to configure publishing for the Gradle plugin.
    if (project.name != "detekt-gradle-plugin") {
        publications.register<MavenPublication>(DETEKT_PUBLICATION) {
            from(components["java"])
        }
    }
    publications.withType<MavenPublication> {
        artifactId = project.name
        version = Versions.currentOrSnapshot()
        pom {
            description.set("Static code analysis for Kotlin")
            name.set("detekt")
            url.set("https://detekt.dev")
            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("detekt Developers")
                    name.set("detekt Developers")
                    email.set("info@detekt.dev")
                }
            }
            scm {
                url.set("https://github.com/detekt/detekt")
            }
            distributionManagement {
                val nameChanges = mapOf(
                    "detekt-formatting" to "detekt-rules-ktlint-wrapper", // done in https://github.com/detekt/detekt/pull/8474
                    "detekt-report-xml" to "detekt-report-checkstyle", // https://github.com/detekt/detekt/pull/8656
                    "detekt-report-md" to "detekt-report-markdown", // https://github.com/detekt/detekt/pull/8735
                    "detekt-rules-empty" to "detekt-rules-emptyblocks", // https://github.com/detekt/detekt/pull/8888
                    "detekt-rules-documentation" to "detekt-rules-comments", // https://github.com/detekt/detekt/pull/8889
                    "detekt-rules-errorprone" to "detekt-rules-potential-bugs", // https://github.com/detekt/detekt/pull/8887
                )
                val relocationExclusions = listOf(
                    "detekt-report-txt", // https://github.com/detekt/detekt/pull/7470
                    "detekt-sample-extensions",
                )
                if (project.name !in relocationExclusions) {
                    val newArtifactName = if (project.name in nameChanges.keys) {
                        nameChanges[project.name]
                    } else {
                        null
                    }
                    relocation {
                        groupId = "dev.detekt"
                        version = "2.0.0"
                        if (newArtifactName != null) {
                            artifactId = newArtifactName
                        }
                        message = "groupId has been changed to match the detekt website"
                    }
                }
            }
        }
    }
}

if (JavaVersion.current() == JavaVersion.VERSION_1_8) {
    tasks.withType<GenerateMavenPom>().configureEach {
        notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/24765")
    }
}

val signingKey = "SIGNING_KEY".byProperty
val signingPwd = "SIGNING_PWD".byProperty
if (signingKey.isNullOrBlank() || signingPwd.isNullOrBlank()) {
    logger.info("Signing disabled as the GPG key was not found")
} else {
    logger.info("GPG Key found - Signing enabled")
}

signing {
    useInMemoryPgpKeys(signingKey, signingPwd)
    sign(publishing.publications)
    isRequired = !(signingKey.isNullOrBlank() || signingPwd.isNullOrBlank())
}

val String.byProperty: String? get() = providers.gradleProperty(this).orNull
