import com.gradle.publish.PluginConfig
import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import java.util.Date

buildscript {
	repositories {
		mavenCentral()
		mavenLocal()
		jcenter()
	}
}

repositories {
	gradlePluginPortal()
	mavenLocal()
	jcenter()
}

plugins {
	`java-gradle-plugin`
	id("com.gradle.plugin-publish") version "0.9.10"
	id("com.jfrog.bintray") version "1.8.4"
	kotlin("jvm") version "1.2.61"
	`kotlin-dsl`
	id("org.jetbrains.dokka") version "0.9.17"
}

kotlinDslPluginOptions {
	experimentalWarning.set(false)
}

apply {
	plugin("maven-publish")
}

group = "io.gitlab.arturbosch.detekt"
version = "1.0.0.RC9"

val detektGradleVersion: String by project
val jcommanderVersion: String by project
val spekVersion = "1.2.1"
val junitPlatformVersion = "1.2.0"
val assertjVersion = "3.10.0"

dependencies {
	implementation(gradleApi())
	implementation(kotlin("stdlib"))
	implementation(kotlin("reflect"))

	testImplementation("org.assertj:assertj-core:$assertjVersion")
	testImplementation("org.jetbrains.spek:spek-api:$spekVersion")
	testImplementation("org.jetbrains.spek:spek-subject-extension:$spekVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}

gradlePlugin {
	plugins {
		register("detektPlugin") {
			id = "io.gitlab.arturbosch.detekt"
			implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
		}
	}
}

val test by tasks.getting(Test::class) {
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

pluginBundle {
	website = "https://arturbosch.github.io/detekt"
	vcsUrl = "https://github.com/arturbosch/detekt"
	description = "Static code analysis for Kotlin"
	tags = listOf("kotlin", "detekt", "code-analysis", "badsmells", "codesmells")

	plugins {
		register("detektPlugin") {
			id = "io.gitlab.arturbosch.detekt"
			displayName = "Static code analysis for Kotlin"
		}
	}
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

tasks.withType(DokkaTask::class.java) {
	// suppresses undocumented classes but not dokka warnings
	// https://github.com/Kotlin/dokka/issues/229 && https://github.com/Kotlin/dokka/issues/319
	reportUndocumented = false
	outputFormat = "javadoc"
	outputDirectory = "$buildDir/javadoc"
}


val generateDefaultDetektVersionFile by tasks.creating {
	val defaultDetektVersionFile =
			File("$buildDir/generated/src/io/gitlab/arturbosch/detekt", "PluginVersion.kt")

	outputs.file(defaultDetektVersionFile)

	doFirst {
		defaultDetektVersionFile.parentFile.mkdirs()
		defaultDetektVersionFile.writeText("""
			package io.gitlab.arturbosch.detekt

			internal const val DEFAULT_DETEKT_VERSION = "$version"
			"""
				.trimIndent()
		)
	}
}

val mainJavaSourceSet: SourceDirectorySet = sourceSets.getByName("main").java
mainJavaSourceSet.srcDir("$buildDir/generated/src")

tasks.named("compileKotlin").configure {
	dependsOn(generateDefaultDetektVersionFile)
}

val javaConvention = the<JavaPluginConvention>()
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
		groupId = rootProject.group as? String
		artifactId = rootProject.name
		version = rootProject.version as? String
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
