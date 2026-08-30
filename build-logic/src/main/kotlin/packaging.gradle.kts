plugins {
    `java-library`
    `maven-publish`
    signing
}

// This flag is reserved for the final, dedicated 1.x release. See scripts/release-relocation.sh.
val publishRelocationInfo = "publishRelocationInfo".byProperty.toBoolean()

val relocationGroupId = "dev.detekt"
val relocationVersion = "2.0.0"
val migrationGuide = "https://detekt.dev/docs/introduction/migrationguide"

val relocationMessage =
    "detekt 2.x is published under the '$relocationGroupId' group, and some artifacts were renamed. " +
        "See the migration guide at $migrationGuide"

val relocationDescription =
    "DEPRECATED - this is the final detekt 1.x release. detekt 2.x is published under the " +
        "'$relocationGroupId' group, and some artifacts were renamed. Migration guide: $migrationGuide"

val notRelocated = setOf(
    "detekt-report-txt",
    "detekt-sample-extensions",
    "detekt-compiler-plugin",
)

val artifactRenames = mapOf(
    "detekt-formatting" to "detekt-rules-ktlint-wrapper",
    "detekt-report-xml" to "detekt-report-checkstyle",
    "detekt-report-md" to "detekt-report-markdown",
    "detekt-rules-empty" to "detekt-rules-empty-blocks",
    "detekt-rules-documentation" to "detekt-rules-comments",
    "detekt-rules-errorprone" to "detekt-rules-potential-bugs",
)

val MavenPublication.isPluginMarker: Boolean get() = name.endsWith("PluginMarkerMaven")

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
            if (!publishRelocationInfo) {
                from(components["java"])
            }
        }
    }
    publications.withType<MavenPublication> {
        artifactId = project.name
        version = Versions.currentOrSnapshot()
        val relocate = publishRelocationInfo && !isPluginMarker && project.name !in notRelocated
        pom {
            if (publishRelocationInfo) {
                packaging = "pom"
            }
            description.set(if (relocate) relocationDescription else "Static code analysis for Kotlin")
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
            if (relocate) {
                val newArtifactId = artifactRenames[project.name]
                distributionManagement {
                    relocation {
                        groupId = relocationGroupId
                        if (newArtifactId != null) {
                            artifactId = newArtifactId
                        }
                        version = relocationVersion
                        message = relocationMessage
                    }
                }
            }
        }
    }
}

// Gradle Module Metadata cannot express Maven relocation and would take precedence over the POM.
tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = !publishRelocationInfo
}

// Artifacts are contributed both by the Java component and directly by module build scripts, so
// remove them after every project has been evaluated.
if (publishRelocationInfo) {
    afterEvaluate {
        val javaComponent = components["java"] as AdhocComponentWithVariants
        listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements")
            .mapNotNull(configurations::findByName)
            .forEach { javaComponent.withVariantsFromConfiguration(it) { skip() } }
        publishing.publications.withType<MavenPublication>().configureEach {
            setArtifacts(emptyList<Any>())
        }
    }
}

// Plugin markers cannot redirect across plugin IDs.
if (publishRelocationInfo) {
    tasks.withType<AbstractPublishToMaven>().configureEach {
        if (name.contains("PluginMarkerMavenPublication")) {
            enabled = false
        }
    }
}

if (publishRelocationInfo && project.name in notRelocated) {
    tasks.withType<AbstractPublishToMaven>().configureEach {
        enabled = false
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
