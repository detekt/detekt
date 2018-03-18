buildscript {
	repositories {
		mavenLocal()
		jcenter()
	}

	val kotlinVersion by project

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

val kotlinVersion by project
val assertjVersion by project
val usedDetektVersion by project
val junitEngineVersion by project
val junitPlatformVersion by project
val spekVersion by project

dependencies {
	implementation("io.gitlab.arturbosch.detekt:detekt-api:$usedDetektVersion")

	testImplementation("io.gitlab.arturbosch.detekt:detekt-test:$usedDetektVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-api:$junitEngineVersion")
	testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
	testImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
	testImplementation("org.assertj:assertj-core:$assertjVersion")
	testImplementation("org.jetbrains.spek:spek-api:$spekVersion")
	testImplementation("org.jetbrains.spek:spek-subject-extension:$spekVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitEngineVersion")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-console:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
