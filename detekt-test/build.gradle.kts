configurations.implementation.extendsFrom(configurations.kotlinImplementation)
configurations.testImplementation.extendsFrom(configurations.kotlinTest)

val assertjVersion by project
val junitPlatformVersion by project
val spekVersion by project

dependencies {
	implementation(project(":detekt-rules"))
	implementation(project(":detekt-core"))
	implementation("org.assertj:assertj-core:$assertjVersion")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-console:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
