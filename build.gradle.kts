import com.jfrog.bintray.gradle.BintrayExtension
import org.codehaus.groovy.tools.shell.util.Logger.io
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.kotlin.dsl.setValue
import org.jetbrains.dokka.gradle.DokkaTask

import java.util.*

buildscript {
	repositories {
		gradlePluginPortal()
		mavenLocal()
		jcenter()
	}

	val kotlinVersion: String by project

	dependencies {
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
	}
}

plugins {
	id("com.jfrog.bintray") version "1.8.4"
	id("com.github.ben-manes.versions") version "0.20.0"
	id("com.github.johnrengelman.shadow") version "2.0.4" apply false
	id("org.sonarqube") version "2.6.2"
	id("io.gitlab.arturbosch.detekt")
	`kotlin-dsl`
	id("org.jetbrains.dokka") version "0.9.17"
}

tasks.withType<Wrapper> {
	gradleVersion = "4.10"
	distributionType = Wrapper.DistributionType.ALL
}

val detektVersion: String by project

allprojects {
	group = "io.gitlab.arturbosch.detekt"
	version = "$detektVersion"

	repositories {
		mavenLocal()
		jcenter()
		maven(url = "http://dl.bintray.com/jetbrains/spek")
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

	if (this.name in listOf("detekt-cli", "detekt-watch-service", "detekt-generator")) {
		apply {
			plugin("application")
			plugin("com.github.johnrengelman.shadow")
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
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

	val javaConvention = the<JavaPluginConvention>()
	tasks.withType(DokkaTask::class.java) {
		// suppresses undocumented classes but not dokka warnings
		// https://github.com/Kotlin/dokka/issues/229 && https://github.com/Kotlin/dokka/issues/319
		reportUndocumented = false
		outputFormat = "javadoc"
		outputDirectory = "$buildDir/javadoc"
	}

	val sourcesJar by tasks.creating(Jar::class) {
		dependsOn("classes")
		classifier = "sources"
		from(javaConvention.sourceSets["main"].allSource)
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

	val kotlinVersion: String  by project
	val junitEngineVersion: String by project
	val assertjVersion: String by project
	val spekVersion: String by project
	val kotlinImplementation by configurations.creating
	val kotlinTest by configurations.creating

	dependencies {
		kotlinImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
		kotlinImplementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
		kotlinTest("org.junit.jupiter:junit-jupiter-api:$junitEngineVersion")
		kotlinTest("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
		kotlinTest("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
		kotlinTest("org.assertj:assertj-core:$assertjVersion")
		kotlinTest("org.jetbrains.spek:spek-api:$spekVersion")
		kotlinTest("org.jetbrains.spek:spek-subject-extension:$spekVersion")
		kotlinTest("org.junit.jupiter:junit-jupiter-engine:$junitEngineVersion")
		kotlinTest("org.reflections:reflections:0.9.11")
	}

	the<JavaPluginConvention>().sourceSets {
		"main" {
			java {
				srcDirs("src/main/kotlin")
			}
		}
	}
}

/**
 * Usage: <code>./gradlew build -PwarningsAsErrors=true</code>.
 */
fun shouldTreatCompilerWarningsAsErrors(): Boolean {
	return project.findProperty("warningsAsErrors") == "true"
}
