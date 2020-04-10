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
    kotlin("jvm")
    `maven-publish`
    jacoco
    id("io.gitlab.arturbosch.detekt")
    id("com.jfrog.artifactory") apply false
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka") apply false
    id("com.github.ben-manes.versions")
    id("com.github.johnrengelman.shadow") apply false
    id("org.sonarqube")
    id("com.github.breadmoirai.github-release")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

val projectJvmTarget = "1.8"

val detektVersion: String by project
val assertjVersion: String by project
val spekVersion: String by project
val reflectionsVersion: String by project
val mockkVersion: String by project
val junitPlatformVersion: String by project

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
        kotlinOptions.freeCompilerArgs = listOf(
            "-progressive",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
        // Usage: <code>./gradlew build -PwarningsAsErrors=true</code>.
        kotlinOptions.allWarningsAsErrors = project.findProperty("warningsAsErrors") == "true"
    }

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))

        testImplementation("org.assertj:assertj-core:$assertjVersion")
        testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
        testImplementation("org.reflections:reflections:$reflectionsVersion")
        testImplementation("io.mockk:mockk:$mockkVersion")

        testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
        testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    }
}

// fat jar applications

configure(listOf(project(":detekt-cli"), project(":detekt-generator"))) {
    apply {
        plugin("application")
        plugin("com.github.johnrengelman.shadow")
    }
}

// jacoco code coverage section

val jacocoVersion: String by project
jacoco.toolVersion = jacocoVersion

tasks {
    jacocoTestReport {
        executionData.setFrom(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        subprojects
            .filterNot { it.name in setOf("detekt-test", "detekt-sample-extensions") }
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

configure(listOf(project(":detekt-test"), project(":detekt-sample-extensions"))) {
    apply {
        plugin("jacoco")
    }
    jacoco.toolVersion = jacocoVersion
}

// publishing section

subprojects {

    apply {
        plugin("com.jfrog.bintray")
        plugin("com.jfrog.artifactory")
        plugin("maven-publish")
    }

    val bintrayUser = findProperty("bintrayUser")?.toString()
        ?: System.getenv("BINTRAY_USER")
    val bintrayKey = findProperty("bintrayKey")?.toString()
        ?: System.getenv("BINTRAY_API_KEY")
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
}

// detekt self analysis section

val analysisDir = file(projectDir)
val baselineFile = file("$rootDir/config/detekt/baseline.xml")
val configFile = file("$rootDir/config/detekt/detekt.yml")
val formatConfigFile = file("$rootDir/config/detekt/format.yml")
val statisticsConfigFile = file("$rootDir/config/detekt/statistics.yml")

val kotlinFiles = "**/*.kt"
val kotlinScriptFiles = "**/*.kts"
val resourceFiles = "**/resources/**"
val buildFiles = "**/build/**"

subprojects {

    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    tasks.withType<Detekt> {
        jvmTarget = projectJvmTarget
    }

    val userHome = System.getProperty("user.home")

    detekt {
        buildUponDefaultConfig = true
        baseline = baselineFile

        reports {
            xml.enabled = true
            html.enabled = true
            txt.enabled = true
        }

        idea {
            path = "$userHome/.idea"
            codeStyleScheme = "$userHome/.idea/idea-code-style.xml"
            inspectionsProfile = "$userHome/.idea/inspect.xml"
            report = "${project.projectDir}/reports"
            mask = "*.kt"
        }
    }
}

allprojects {

    dependencies {
        detekt(project(":detekt-cli"))
        detektPlugins(project(":detekt-formatting"))
        detektPlugins("io.github.mkohm:detekt-hint:0.1.4")
    }
}

val detektFormat by tasks.registering(Detekt::class) {
    description = "Formats whole project."
    parallel = true
    disableDefaultRuleSets = true
    buildUponDefaultConfig = true
    autoCorrect = true
    setSource(analysisDir)
    config.setFrom(listOf(statisticsConfigFile, formatConfigFile))
    include(kotlinFiles)
    include(kotlinScriptFiles)
    exclude(resourceFiles)
    exclude(buildFiles)
    baseline.set(baselineFile)
    reports {
        xml.enabled = false
        html.enabled = false
        txt.enabled = false
    }
}

val detektAll by tasks.registering(Detekt::class) {
    description = "Runs the whole project at once."
    parallel = true
    buildUponDefaultConfig = true
    setSource(analysisDir)
    config.setFrom(listOf(statisticsConfigFile, configFile))
    include(kotlinFiles)
    include(kotlinScriptFiles)
    exclude(resourceFiles)
    exclude(buildFiles)
    baseline.set(baselineFile)
    reports {
        xml.enabled = false
        html.enabled = false
        txt.enabled = false
    }
}

task<Detekt>("detektHint") {
    description = "Running detekt-hint and outputs report for Danger to consume."
    setSource(analysisDir)
    config.setFrom(file("$rootDir/config/detekt/detekt-hint.yml"))
    classpath.setFrom(detektClasspath)
    reports {
        xml {
            enabled = true
            destination = file("$buildDir/reports/detekt-hint-report.xml")
        }
        html.enabled = false
        txt.enabled = false
    }
    include(kotlinFiles)
    exclude(resourceFiles)
    exclude(buildFiles)
}

val detektProjectBaseline by tasks.registering(DetektCreateBaselineTask::class) {
    description = "Overrides current baseline."
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(analysisDir)
    config.setFrom(listOf(statisticsConfigFile, configFile))
    include(kotlinFiles)
    include(kotlinScriptFiles)
    exclude(resourceFiles)
    exclude(buildFiles)
    baseline.set(baselineFile)
}

// release section

githubRelease {
    token(project.findProperty("github.token") as? String ?: "")
    owner.set("arturbosch")
    repo.set("detekt")
    overwrite.set(true)
    dryRun.set(false)
    body {
        var changelog = project.file("docs/pages/changelog 1.x.x.md").readText()
        val sectionStart = "#### ${project.version}"
        changelog = changelog.substring(changelog.indexOf(sectionStart) + sectionStart.length)
        changelog = changelog.substring(0, changelog.indexOf("#### 1"))
        changelog.trim()
    }
    releaseAssets.setFrom(project(":detekt-cli").buildDir.resolve("libs/detekt-cli-${project.version}-all.jar"))
}
