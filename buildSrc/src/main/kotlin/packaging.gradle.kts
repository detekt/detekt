import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.jfrog.bintray.gradle.BintrayExtension
import groovy.lang.GroovyObject
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import java.util.Date

plugins {
    `java-library` apply false // is applied in commons; make configurations available in this script
    `maven-publish` apply false
    id("com.jfrog.artifactory") apply false
    id("com.jfrog.bintray") apply false
}

project(":detekt-cli") {
    apply {
        plugin("application")
        plugin("com.github.johnrengelman.shadow")
    }

    tasks.withType<ShadowJar>() {
        mergeServiceFiles()
    }
}

subprojects {

    apply {
        plugin("maven-publish")
        plugin("com.jfrog.bintray")
        plugin("com.jfrog.artifactory")
    }

    val bintrayUser = findProperty("bintrayUser")?.toString()
        ?: System.getenv("BINTRAY_USER")
    val bintrayKey = findProperty("bintrayKey")?.toString()
        ?: System.getenv("BINTRAY_API_KEY")

    bintray {
        user = bintrayUser
        key = bintrayKey
        val mavenCentralUser = System.getenv("MAVEN_CENTRAL_USER") ?: ""
        val mavenCentralPassword = System.getenv("MAVEN_CENTRAL_PW") ?: ""

        setPublications(DETEKT_PUBLICATION)

        pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
            repo = "code-analysis"
            name = "detekt"
            userOrg = "arturbosch"
            setLicenses("Apache-2.0")
            vcsUrl = "https://github.com/detekt/detekt"

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

    publishing {
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

    configure<ArtifactoryPluginConvention> {
        setContextUrl("https://oss.jfrog.org/artifactory")
        publish(delegateClosureOf<PublisherConfig> {
            repository(delegateClosureOf<GroovyObject> {
                setProperty("repoKey", "oss-snapshot-local")
                setProperty("username", bintrayUser)
                setProperty("password", bintrayKey)
                setProperty("maven", true)
            })
            defaults(delegateClosureOf<GroovyObject> {
                invokeMethod("publications", DETEKT_PUBLICATION)
                setProperty("publishArtifacts", true)
                setProperty("publishPom", true)
            })
        })
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
