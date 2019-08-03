import com.jfrog.bintray.gradle.BintrayExtension
import groovy.lang.GroovyObject
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import java.util.Date

plugins {
    id("com.gradle.build-scan") version "2.4"
    kotlin("jvm") version "1.3.41"
    id("com.jfrog.bintray") version "1.8.4"
    id("com.jfrog.artifactory") version "4.9.8" apply false
    id("com.github.ben-manes.versions") version "0.22.0"
    id("com.github.johnrengelman.shadow") version "5.1.0" apply false
    id("org.sonarqube") version "2.7"
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka") version "0.9.18" apply false
    jacoco
    `maven-publish`
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

tasks.wrapper {
    gradleVersion = "5.6"
    distributionType = Wrapper.DistributionType.ALL
    doLast {
        /*
         * Copy the properties file into the detekt-gradle-plugin project.
         * This allows IDEs like IntelliJ to import the detekt-gradle-plugin as a standalone project.
         */
        copy {
            from(propertiesFile)
            into(file("${gradle.includedBuild("detekt-gradle-plugin").projectDir}/gradle/wrapper"))
        }
    }
}

tasks.check {
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":check"))
}

tasks.withType<Detekt>().configureEach {
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":detekt"))
}

val jacocoVersion: String by project
jacoco.toolVersion = jacocoVersion

tasks {
    jacocoTestReport {
        executionData.setFrom(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        subprojects
            .filterNot { it.name in listOf("detekt-test", "detekt-sample-extensions") }
            .forEach {
                this@jacocoTestReport.sourceSets(it.sourceSets.main.get())
                this@jacocoTestReport.dependsOn(it.tasks.test)
            }

        reports {
            xml.isEnabled = true
            xml.destination = file("$buildDir/reports/jacoco/report.xml")
        }
    }
}

val detektVersion: String by project

allprojects {
    group = "io.gitlab.arturbosch.detekt"
    version = detektVersion + if (System.getProperty("snapshot")?.toBoolean() == true) "-SNAPSHOT" else ""

    repositories {
        jcenter()
    }
}

subprojects {

    val project = this

    apply {
        plugin("java-library")
        plugin("kotlin")
        plugin("com.jfrog.bintray")
        plugin("com.jfrog.artifactory")
        plugin("maven-publish")
        plugin("io.gitlab.arturbosch.detekt")
    }

    if (project.name !in listOf("detekt-test", "detekt-sample-extensions")) {
        apply {
            plugin("jacoco")
        }
        jacoco.toolVersion = jacocoVersion
    }

    tasks.withType<Detekt>().configureEach {
        exclude("resources/")
        exclude("build/")
    }

    val userHome = System.getProperty("user.home")

    detekt {
        debug = true
        buildUponDefaultConfig = true
        baseline = file("$rootDir/config/detekt/baseline.xml")

        reports {
            xml.enabled = true
            html.enabled = true
            txt.enabled = true
        }

        idea {
            path = "$userHome/.idea"
            codeStyleScheme = "$userHome/.idea/idea-code-style.xml"
            inspectionsProfile = "$userHome/.idea/inspect.xml"
            report = "project.projectDir/reports"
            mask = "*.kt"
        }
    }

    val shadowedProjects = listOf("detekt-cli", "detekt-generator")

    if (project.name in shadowedProjects) {
        apply {
            plugin("application")
            plugin("com.github.johnrengelman.shadow")
        }
    }

    tasks.withType<Test>().configureEach {
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

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
        // https://youtrack.jetbrains.com/issue/KT-24946
        kotlinOptions.freeCompilerArgs = listOf(
            "-progressive",
            "-Xskip-runtime-version-check",
            "-Xdisable-default-scripting-plugin",
            "-Xuse-experimental=kotlin.Experimental"
        )
        kotlinOptions.allWarningsAsErrors = shouldTreatCompilerWarningsAsErrors()
    }

    val bintrayUser = findProperty("bintrayUser")?.toString() ?: System.getenv("BINTRAY_USER")
    val bintrayKey = findProperty("bintrayKey")?.toString() ?: System.getenv("BINTRAY_API_KEY")
    val detektPublication = "DetektPublication"

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

    val sourcesJar by tasks.creating(Jar::class) {
        dependsOn(tasks.classes)
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by tasks.creating(Jar::class) {
        from(tasks.javadoc)
        archiveClassifier.set("javadoc")
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
    }

    publishing {
        publications.create<MavenPublication>(detektPublication) {
            from(components["java"])
            artifact(sourcesJar)
            artifact(javadocJar)
            if (project.name == "detekt-cli") {
                artifact(tasks.getByName("shadowJar"))
            }
            groupId = this@subprojects.group as? String
            artifactId = this@subprojects.name
            version = this@subprojects.version as? String
            pom {
                description.set("Static code analysis for Kotlin")
                name.set("detekt")
                url.set("https://arturbosch.github.io/detekt")
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
                invokeMethod("publications", detektPublication)
                setProperty("publishArtifacts", true)
                setProperty("publishPom", true)
            })
        })
    }

    val assertjVersion: String by project
    val spekVersion: String by project
    val kotlinTest by configurations.creating

    dependencies {
        implementation(kotlin("stdlib"))

        detekt(project(":detekt-cli"))
        detektPlugins(project(":detekt-formatting"))

        kotlinTest("org.assertj:assertj-core:$assertjVersion")
        kotlinTest("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    }

    sourceSets.main.get().java.srcDirs("src/main/kotlin")
}

/**
 * Usage: <code>./gradlew build -PwarningsAsErrors=true</code>.
 */
fun shouldTreatCompilerWarningsAsErrors(): Boolean {
    return project.findProperty("warningsAsErrors") == "true"
}

dependencies {
    detekt(project(":detekt-cli"))
    detektPlugins(project(":detekt-formatting"))
}

val detektFormat by tasks.registering(Detekt::class) {
    description = "Reformats whole code base."
    parallel = true
    disableDefaultRuleSets = true
    buildUponDefaultConfig = true
    autoCorrect = true
    setSource(files(projectDir))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    config = files("$rootDir/config/detekt/format.yml")
    reports {
        xml.enabled = false
        html.enabled = false
        txt.enabled = false
    }
}

val detektAll by tasks.registering(Detekt::class) {
    description = "Runs over whole code base without the starting overhead for each module."
    parallel = true
    buildUponDefaultConfig = true
    setSource(files(projectDir))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    baseline.set(file("$rootDir/config/detekt/baseline.xml"))
    reports {
        xml.enabled = false
        html.enabled = false
        txt.enabled = false
    }
}
