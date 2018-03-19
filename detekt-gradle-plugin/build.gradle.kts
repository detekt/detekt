plugins {
	`java-gradle-plugin`
	id("com.gradle.plugin-publish")
}

val detektGradleVersion by project

version = "$detektGradleVersion"

configurations.implementation.extendsFrom(configurations.kotlinImplementation)
configurations.testImplementation.extendsFrom(configurations.kotlinTest)

val jcommanderVersion by project
val spekVersion by project
val junitPlatformVersion by project

dependencies {
	implementation(gradleApi())
	implementation(project(":detekt-cli"))
	implementation("com.beust:jcommander:$jcommanderVersion")
	
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
