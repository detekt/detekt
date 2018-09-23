buildscript {
	repositories {
		mavenLocal()
		jcenter()
	}

	val kotlinVersion: String by project

	dependencies {
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
	}
}

repositories {
	jcenter()
}

apply {
	plugin("kotlin")
}

val assertjVersion: String by project
val usedDetektVersion: String by project
val junitEngineVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
	implementation("io.gitlab.arturbosch.detekt:detekt-api:$usedDetektVersion")

	testImplementation("io.gitlab.arturbosch.detekt:detekt-test:$usedDetektVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-api:$junitEngineVersion")
	testImplementation(kotlin("test"))
	testImplementation(kotlin("reflect"))
	testImplementation("org.assertj:assertj-core:$assertjVersion")
	testImplementation("org.jetbrains.spek:spek-api:$spekVersion")
	testImplementation("org.jetbrains.spek:spek-subject-extension:$spekVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitEngineVersion")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
