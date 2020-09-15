import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library` apply false // is applied in commons; make configurations available in this script
    `maven-publish` apply false
    signing apply false
    id("io.codearte.nexus-staging")
}

val sonatypeUsername: String? = findProperty("sonatypeUsername")
    ?.toString()
    ?: System.getenv("MAVEN_CENTRAL_USER")
val sonatypePassword: String? = findProperty("sonatypePassword")
    ?.toString()
    ?: System.getenv("MAVEN_CENTRAL_PW")

nexusStaging {
    packageGroup = "io.gitlab.arturbosch"
    stagingProfileId = "1d8efc8232c5c"
    username = sonatypeUsername
    password = sonatypePassword
}

project(":detekt-cli") {
    apply {
        plugin("application")
        plugin("com.github.johnrengelman.shadow")
    }

    tasks.withType<ShadowJar>().configureEach {
        mergeServiceFiles()
    }
}

subprojects {

    apply {
        plugin("maven-publish")
        plugin("signing")
    }

    publishing {
        repositories {
            maven {
                name = "mavenCentral"
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
            maven {
                name = "sonatypeSnapshot"
                url = uri("https://oss.sonatype.org/content/repositories/snapshots")
                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
        }
        publications.register<MavenPublication>(DETEKT_PUBLICATION) {
            groupId = project.group as? String
            artifactId = project.name
            version = project.version as? String
            pom {
                description.set("Static code analysis for Kotlin")
                name.set("detekt")
                url.set("https://detekt.github.io/detekt")
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
                    url.set("https://github.com/detekt/detekt")
                }
            }
        }
    }

    if (findProperty("signing.keyId") != null) {
        signing {
            sign(publishing.publications[DETEKT_PUBLICATION])
        }
    } else {
        logger.info("Signing Disabled as the PGP key was not found")
    }
}

configure(subprojects.filter { it.name != "detekt-bom" }) {
    val sourcesJar by tasks.registering(Jar::class) {
        dependsOn(tasks.classes)
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by tasks.registering(Jar::class) {
        from(tasks.javadoc)
        archiveClassifier.set("javadoc")
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
    }

    publishing {
        publications.named<MavenPublication>(DETEKT_PUBLICATION) {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
            if (project.name == "detekt-cli") {
                artifact(tasks.getByName("shadowJar"))
            }
        }
    }
}
