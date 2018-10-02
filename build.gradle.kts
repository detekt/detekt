import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.util.GFileUtils
import org.jetbrains.dokka.gradle.DokkaTask

import java.util.Date

plugins {
	kotlin("jvm") version "1.2.61"
	id("com.jfrog.bintray") version "1.8.4"
	id("com.github.ben-manes.versions") version "0.20.0"
	id("com.github.johnrengelman.shadow") version "2.0.4" apply false
	id("org.sonarqube") version "2.6.2"
	id("io.gitlab.arturbosch.detekt")
	id("org.jetbrains.dokka") version "0.9.17"
}

tasks.withType<Wrapper> {
	gradleVersion = "4.10.1"
	distributionType = Wrapper.DistributionType.ALL
	doLast {
		/*
		 * Copy the properties file into the detekt-gradle-plugin project.
		 * This allows IDEs like IntelliJ to import the detekt-gradle-plugin as a standalone project.
		 */
		val gradlePluginWrapperDir = File(gradle.includedBuild("detekt-gradle-plugin").projectDir, "/gradle/wrapper")
		GFileUtils.mkdirs(gradlePluginWrapperDir)
		copy {
			from(propertiesFile)
			into(gradlePluginWrapperDir)
		}
	}
}

tasks.withType<Test> {
	dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":test"))
}

val detektVersion: String by project
val usedDetektVersion: String by project

allprojects {
	group = "io.gitlab.arturbosch.detekt"
	version = detektVersion

	repositories {
		mavenLocal()
		jcenter()
		maven(url = "http://dl.bintray.com/jetbrains/spek")
		maven(url = "https://dl.bintray.com/arturbosch/generic")
	}
}

subprojects {

	apply {
		plugin("java-library")
		plugin("kotlin")
		plugin("com.jfrog.bintray")
		plugin("maven-publish")
		plugin("io.gitlab.arturbosch.detekt")
		plugin("org.jetbrains.dokka")
	}

	val userHome = System.getProperty("user.home")

	detekt {
		debug = true
		toolVersion = usedDetektVersion
		config = files(
				project.rootDir.resolve("detekt-cli/src/main/resources/default-detekt-config.yml"),
				project.rootDir.resolve("reports/failfast.yml")
		)
		filters = ".*/resources/.*,.*/build/.*"
		baseline = project.rootDir.resolve("reports/baseline.xml")

		reports {
			xml.enabled = true
			html.enabled = true
		}

		idea {
			path = "$userHome/.idea"
			codeStyleScheme = "$userHome/.idea/idea-code-style.xml"
			inspectionsProfile = "$userHome/.idea/inspect.xml"
			report = "project.projectDir/reports"
			mask = "*.kt"
		}
	}

	if (this.name in listOf("detekt-cli", "detekt-watcher", "detekt-generator")) {
		apply {
			plugin("application")
			plugin("com.github.johnrengelman.shadow")
		}
	}

	tasks.withType<Test> {
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

	tasks.withType<KotlinCompile> {
		kotlinOptions.jvmTarget = "1.8"
		// https://youtrack.jetbrains.com/issue/KT-24946
		kotlinOptions.freeCompilerArgs = listOf("-Xskip-runtime-version-check", "-Xdisable-default-scripting-plugin")
		kotlinOptions.allWarningsAsErrors = shouldTreatCompilerWarningsAsErrors()
	}

	bintray {
		user = System.getenv("BINTRAY_USER") ?: ""
		key = System.getenv("BINTRAY_API_KEY") ?: ""
		val mavenCentralUser = System.getenv("MAVEN_CENTRAL_USER") ?: ""
		val mavenCentralPassword = System.getenv("MAVEN_CENTRAL_PW") ?: ""

		setPublications("DetektPublication")

		override = (project.version as? String)?.endsWith("-SNAPSHOT") == true

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

	tasks.withType(DokkaTask::class.java) {
		// suppresses undocumented classes but not dokka warnings
		// https://github.com/Kotlin/dokka/issues/229 && https://github.com/Kotlin/dokka/issues/319
		reportUndocumented = false
		outputFormat = "javadoc"
		outputDirectory = "$buildDir/javadoc"
		// Java 8 is only version supported both by Oracle/OpenJDK and Dokka itself
		// https://github.com/Kotlin/dokka/issues/294
		enabled = JavaVersion.current().isJava8
	}

	val sourcesJar by tasks.creating(Jar::class) {
		dependsOn("classes")
		classifier = "sources"
		from(sourceSets["main"].allSource)
	}

	val javadocJar by tasks.creating(Jar::class) {
		dependsOn("dokka")
		classifier = "javadoc"
		from(buildDir.resolve("javadoc"))
	}

	artifacts {
		add("archives", sourcesJar)
		add("archives", javadocJar)
	}

	configure<PublishingExtension> {
		publications.create<MavenPublication>("DetektPublication") {
			from(components["java"])
			artifact(sourcesJar)
			artifact(javadocJar)
			groupId = this@subprojects.group as? String
			artifactId = this@subprojects.name
			version = this@subprojects.version as? String
			pom.withXml {
				asNode().apply {
					appendNode("description", "Static code analysis for Kotlin")
					appendNode("name", "detekt")
					appendNode("url", "https://arturbosch.github.io/detekt")

					val license = appendNode("licenses").appendNode("license")
					license.appendNode("name", "The Apache Software License, Version 2.0")
					license.appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")
					license.appendNode("distribution", "repo")

					val developer = appendNode("developers").appendNode("developer")
					developer.appendNode("id", "Artur Bosch")
					developer.appendNode("name", "Artur Bosch")
					developer.appendNode("email", "arturbosch@gmx.de")

					appendNode("scm").appendNode("url", "https://github.com/arturbosch/detekt")
				}
			}
		}
	}

	val junitEngineVersion: String by project
	val assertjVersion: String by project
	val spekVersion: String by project
	val kotlinTest by configurations.creating

	dependencies {
		implementation(kotlin("stdlib"))
		kotlinTest(kotlin("test"))
		kotlinTest("org.junit.jupiter:junit-jupiter-api:$junitEngineVersion")
		kotlinTest("org.assertj:assertj-core:$assertjVersion")
		kotlinTest("org.jetbrains.spek:spek-api:$spekVersion")
		kotlinTest("org.jetbrains.spek:spek-subject-extension:$spekVersion")
		kotlinTest("org.junit.jupiter:junit-jupiter-engine:$junitEngineVersion")
	}

	sourceSets["main"].java.srcDirs("src/main/kotlin")
}

/**
 * Usage: <code>./gradlew build -PwarningsAsErrors=true</code>.
 */
fun shouldTreatCompilerWarningsAsErrors(): Boolean {
	return project.findProperty("warningsAsErrors") == "true"
}
