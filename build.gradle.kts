import com.jfrog.bintray.gradle.BintrayExtension
import groovy.lang.GroovyObject
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import java.util.Date

plugins {
    id("io.gitlab.arturbosch.detekt")
    jacoco
    `maven-publish`
    id("com.jfrog.artifactory") apply false
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka") apply false
    id("com.github.ben-manes.versions")
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") apply false
    id("org.sonarqube")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

val detektVersion: String by project
val assertjVersion: String by project
val spekVersion: String by project
val reflectionsVersion: String by project
val mockkVersion: String by project
val junitPlatformVersion: String by project
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

fun versionOrSnapshot(): String {
    if (System.getProperty("snapshot")?.toBoolean() == true) {
        return "$detektVersion-SNAPSHOT"
    }
    return detektVersion
}

allprojects {
    group = "io.gitlab.arturbosch.detekt"
    version = versionOrSnapshot()

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

    val projectJvmTarget = "1.8"

    tasks.withType<Detekt> {
        jvmTarget = projectJvmTarget
    }

    val userHome = System.getProperty("user.home")

    detekt {
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

    tasks.withType<Test> {
        useJUnitPlatform()
        systemProperty("SPEK_TIMEOUT", 0) // disable test timeout
        val compileSnippetText: Boolean = if (project.hasProperty("compile-test-snippets")) {
            (project.property("compile-test-snippets") as String).toBoolean()
        } else {
            false
        }
        systemProperty("compile-snippet-tests", compileSnippetText)
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

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = projectJvmTarget
        // https://youtrack.jetbrains.com/issue/KT-24946
        kotlinOptions.freeCompilerArgs = listOf(
            "-progressive",
            "-Xskip-runtime-version-check",
            "-Xdisable-default-scripting-plugin",
            "-Xopt-in=kotlin.RequiresOptIn"
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
        publications.register<MavenPublication>(detektPublication) {
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

    dependencies {
        implementation(kotlin("stdlib"))

        detekt(project(":detekt-cli"))
        detektPlugins(project(":detekt-formatting"))

        testImplementation("org.assertj:assertj-core:$assertjVersion")
        testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
        testImplementation("org.reflections:reflections:$reflectionsVersion")
        testImplementation("io.mockk:mockk:$mockkVersion")

        testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
        testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    }
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
    config.setFrom(files("$rootDir/config/detekt/format.yml"))
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

val detektProjectBaseline by tasks.registering(DetektCreateBaselineTask::class) {
    description = "Overrides current baseline."
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(files(rootDir))
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline.set(file("$rootDir/config/detekt/baseline.xml"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
}
