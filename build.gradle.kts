import com.jfrog.bintray.gradle.BintrayExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.ProfileExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.junit.platform.console.options.Details
import org.junit.platform.gradle.plugin.JUnitPlatformExtension
import java.util.*

buildscript {
	repositories {
		gradlePluginPortal()
		jcenter()
		mavenLocal()
	}

	val kotlinVersion by project
	val junitPlatformVersion by project
	val usedDetektGradleVersion by project

	dependencies {
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
		classpath("org.junit.platform:junit-platform-gradle-plugin:$junitPlatformVersion")
		classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$usedDetektGradleVersion")
	}
}

plugins {
	id("com.jfrog.bintray") version "1.8.0"
	id("com.github.ben-manes.versions") version "0.17.0"
	id("com.github.johnrengelman.shadow") version "2.0.2" apply false
	id("org.sonarqube") version "2.6.2"
	id("com.gradle.plugin-publish") version "0.9.10" apply false
}

apply {
	plugin("io.gitlab.arturbosch.detekt")
}

tasks.withType<Wrapper> {
	gradleVersion = "4.6"
	distributionType = Wrapper.DistributionType.ALL
}

val detektVersion by project

allprojects {
	group = "io.gitlab.arturbosch.detekt"
	version = "$detektVersion"

	repositories {
		jcenter()
		mavenLocal()
		maven ( url = "http://dl.bintray.com/jetbrains/spek" )
	}
}

subprojects {

	apply {
		plugin("org.junit.platform.gradle.plugin")
		plugin("java-library")
		plugin("kotlin")
		plugin("com.jfrog.bintray")
		plugin("maven-publish")
	}

	if (this.name in listOf("detekt-cli", "detekt-watch-service", "detekt-generator")) {
		apply {
			plugin("application")
			plugin("com.github.johnrengelman.shadow")
		}
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions.jvmTarget = "1.8"
		kotlinOptions.freeCompilerArgs = listOf("-Xskip-runtime-version-check")
		kotlinOptions.allWarningsAsErrors = shouldTreatCompilerWarningsAsErrors()
	}

	configure<JUnitPlatformExtension> {
		details = Details.TREE
		filters {
			engines {
				include = listOf("spek", "junit-jupiter")
			}
		}
	}

	bintray {
		user = System.getenv("BINTRAY_USER") ?: ""
		key = System.getenv("BINTRAY_API_KEY") ?: ""
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
			})
		})
	}

	val sourcesJar by tasks.creating(Jar::class) {
		dependsOn("classes")
		classifier = "sources"
		from(the<JavaPluginConvention>().sourceSets["main"].allSource)
	}

	artifacts {
		add("archives", sourcesJar)
	}


	configure<PublishingExtension> {
		publications.create<MavenPublication>("DetektPublication") {
			from(components["java"])
			artifact(sourcesJar)
			groupId = this@subprojects.group as? String
			artifactId = this@subprojects.name
			version = this@subprojects.version as? String
			pom.withXml {
				asNode().apply {
					appendNode("description", "Static code analysis for Kotlin")
					appendNode("name", "detekt")
					appendNode("url", "https://github.com/arturbosch/detekt")

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

	val kotlinVersion by project
	val junitEngineVersion by project
	val junitPlatformVersion by project
	val assertjVersion by project
	val spekVersion by project
	val kotlinImplementation by configurations.creating
	val kotlinTest by configurations.creating
	val junitPlatform = configurations["junitPlatform"]

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
		junitPlatform("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
		junitPlatform("org.junit.platform:junit-platform-console:$junitPlatformVersion")
		junitPlatform("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
	}

	the<JavaPluginConvention>().sourceSets {
		"main" {
			java {
				srcDirs("src/main/kotlin")
			}
		}
	}
}

val userHome: String = System.getProperty("user.home")

val usedDetektVersion by project

configure<DetektExtension>{

	debug = true
	version = "$usedDetektVersion"
	profile = "failfast"

	profile("main", Action {
		input = rootProject.projectDir.absolutePath
		filters = ".*/resources/.*, .*/build/.*"
		config = "${rootProject.projectDir}/detekt-cli/src/main/resources/default-detekt-config.yml"
		baseline = "${rootProject.projectDir}/reports/baseline.xml"
	})

	profile("failfast", Action {
		input = rootProject.projectDir.absolutePath
		config = "${rootProject.projectDir}/reports/failfast.yml"
	})

	profile("output", Action {
		output = "${rootProject.projectDir}/reports"
		outputName = "detekt"
	})

	idea(Action {
		path = "$userHome/.idea"
		codeStyleScheme = "$userHome/.idea/idea-code-style.xml"
		inspectionsProfile = "$userHome/.idea/inspect.xml"
		report = "${rootProject.projectDir}/reports"
		mask = "*.kt,"
	})
}

/**
 * Usage: <code>./gradlew build -PwarningsAsErrors=true</code>.
 */
fun shouldTreatCompilerWarningsAsErrors() : Boolean {
	return project.findProperty("warningsAsErrors") == "true"
}
