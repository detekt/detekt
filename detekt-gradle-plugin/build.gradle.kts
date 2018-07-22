buildscript {
	repositories {
		mavenCentral()
		jcenter()
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

group = "io.gitlab.arturbosch.detekt"
version = "1.0.0.RC7-3"

val spekVersion = "1.1.5"
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
	(plugins) {
		"detektPlugin" {
			id = "io.gitlab.arturbosch.detekt"
			implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
		}
	}
}

val test by tasks.getting(Test::class) {
	useJUnitPlatform()
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
