plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
}

publishing {
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
            description = "Static code analysis for Kotlin"
            name = "detekt"
            url = "https://detekt.dev"
            licenses {
                license {
                    name = "The Apache Software License, Version 2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    distribution = "repo"
                }
            }
            developers {
                developer {
                    id = "detekt Developers"
                    name = "detekt Developers"
                    email = "info@detekt.dev"
                }
            }
            scm {
                url = "https://github.com/detekt/detekt"
            }
        }
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
