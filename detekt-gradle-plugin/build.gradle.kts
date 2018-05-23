buildscript {
	repositories {
		mavenCentral()
		jcenter()
	}
	dependencies {
		classpath("org.junit.platform:junit-platform-gradle-plugin:1.1.0")
	}
}

repositories {
	gradlePluginPortal()
	jcenter()
}

plugins {
	`java-gradle-plugin`
	id("com.gradle.plugin-publish") version "0.9.10"
	kotlin("jvm") version "1.2.41"
}

apply {
	plugin("org.junit.platform.gradle.plugin")
}

version = "1.0.0.RC7"

val spekVersion by project
val junitPlatformVersion by project
val assertjVersion by project

dependencies {
	implementation(gradleApi())
	implementation(kotlin("stdlib"))
	implementation(kotlin("reflect"))
	
	testImplementation("org.assertj:assertj-core:$assertjVersion")
	testImplementation("org.jetbrains.spek:spek-api:$spekVersion")
	testImplementation("org.jetbrains.spek:spek-subject-extension:$spekVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-console:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}


gradlePlugin {
	(plugins) {
		"detektPlugin" {
			id = "io.gitlab.arturbosch.detekt"
			implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
		}
	}
}


pluginBundle {
	website = "https://github.com/arturbosch/detekt"
	vcsUrl = "https://github.com/arturbosch/detekt"
	description = "Static code analysis for Kotlin"
	tags = listOf("kotlin", "detekt", "code-analysis", "badsmells", "codesmells")

	(plugins) {
		"detektPlugin" {
			id = "io.gitlab.arturbosch.detekt"
			displayName = "Static code analysis for Kotlin"
		}
	}
}
