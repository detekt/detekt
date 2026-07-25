plugins {
    id("java-library")
    id("com.vanniktech.maven.publish")
}

val signingKey = providers.gradleProperty("signingInMemoryKey").orNull
if (signingKey.isNullOrBlank()) {
    logger.info("Signing disabled as the GPG key was not found")
} else {
    logger.info("GPG Key found - Signing enabled")
}

mavenPublishing {
    publishToMavenCentral()
    if (!signingKey.isNullOrBlank()) {
        signAllPublications()
    }
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
