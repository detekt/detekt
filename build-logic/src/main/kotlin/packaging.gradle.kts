plugins {
    `java-library`
    `maven-publish`
    signing
}

// A POM carrying <distributionManagement><relocation> is a redirect: every consumer of that version
// is resolved to the new coordinates instead. It must therefore only ever be attached to the final,
// dedicated 1.x release that exists solely to point at detekt 2.x - never to a regular bugfix
// release. See scripts/release-relocation.sh.
val publishRelocationInfo = "publishRelocationInfo".byProperty.toBoolean()

// The group all detekt artifacts moved to in 2.x.
val relocationGroupId = "dev.detekt"

// The 2.x release the relocation points at. It has to be on Maven Central before we publish this.
val relocationVersion = "2.0.0"

val migrationGuide = "https://detekt.dev/docs/introduction/migrationguide"

// Shown verbatim by Maven ("[WARNING] ... has been relocated ...: <message>"). Gradle ignores it and
// logs its own wording at --info instead, so this cannot be the only place we point at the guide.
val relocationMessage =
    "detekt 2.x is published under the '$relocationGroupId' group, and some artifacts were renamed. " +
        "See the migration guide at $migrationGuide"

// The POM description is what Maven Central's search UI and mvnrepository.com render on the
// artifact page, so it is the one channel a Gradle user is likely to actually read.
val relocationDescription =
    "DEPRECATED - this is the final detekt 1.x release. detekt 2.x is published under the " +
        "'$relocationGroupId' group, and some artifacts were renamed. Migration guide: $migrationGuide"

// Artifacts that have no counterpart in 2.x and are therefore not relocated.
val notRelocated = setOf(
    "detekt-report-txt", // https://github.com/detekt/detekt/pull/7470
    "detekt-sample-extensions",
    "detekt-compiler-plugin", // not released as of 2.0.0
)

// Modules renamed in 2.x: 1.x project name -> 2.x artifactId.
val artifactRenames = mapOf(
    "detekt-formatting" to "detekt-rules-ktlint-wrapper", // https://github.com/detekt/detekt/pull/8474
    "detekt-report-xml" to "detekt-report-checkstyle", // https://github.com/detekt/detekt/pull/8656
    "detekt-report-md" to "detekt-report-markdown", // https://github.com/detekt/detekt/pull/8735
    "detekt-rules-empty" to "detekt-rules-empty-blocks", // https://github.com/detekt/detekt/pull/8888
    "detekt-rules-documentation" to "detekt-rules-comments", // https://github.com/detekt/detekt/pull/8889
    "detekt-rules-errorprone" to "detekt-rules-potential-bugs", // https://github.com/detekt/detekt/pull/8887
)

// Gradle plugin marker publications are never relocated. A marker is derived from the plugin id,
// and the id changed to 'dev.detekt' in 2.x, so a relocated marker would resolve the 2.x plugin jar
// and then fail with "plugin with id 'io.gitlab.arturbosch.detekt' not found".
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
            // The relocation release carries no code. A relocated version's jar is never fetched by
            // any resolver, so attaching the java component would only upload dead weight to
            // Maven Central - and a jar reachable by explicit coordinates that hands out 1.x code
            // with no warning attached.
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

// Relocation only exists in the POM. Gradle prefers Gradle Module Metadata, which has no equivalent
// of it, so publishing the .module files alongside would make the redirect invisible to every
// Gradle consumer - and Gradle is how most people consume detekt. The relocation release therefore
// ships POM metadata only.
tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = !publishRelocationInfo
}

// Keeping jars out of the relocation release takes two passes, because artifacts reach a
// publication by two different routes and at two different times:
//
//  - through the java component, which java-gradle-plugin attaches to detekt-gradle-plugin's
//    publications in its own afterEvaluate - too late to clear the artifact list up front, so the
//    variants have to be stripped off the component itself
//  - added directly by a module's build script, e.g. detekt-cli attaching its shadow jar, which
//    happens after this plugin is applied and so needs a late pass
// Both passes run in afterEvaluate: sourcesElements and javadocElements are only registered on the
// java component once module.gradle.kts has called withSourcesJar()/withJavadocJar().
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

// Gradle plugin markers are not relocated, so in a relocation release they would resolve to a
// version whose plugin jar no longer exists. Publishing them would only turn "no such version" into
// a later, more confusing failure, so the relocation release leaves them out.
if (publishRelocationInfo) {
    // Matched on the task name rather than on `publication`, which is still null while the task is
    // being created.
    tasks.withType<AbstractPublishToMaven>().configureEach {
        if (name.contains("PluginMarkerMavenPublication")) {
            enabled = false
        }
    }
}

// Modules with no 2.x counterpart have nothing to point at, and a code-free release of them would
// be meaningless, so the relocation release skips them altogether.
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
