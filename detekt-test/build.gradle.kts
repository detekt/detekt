configurations.testImplementation.extendsFrom(configurations.kotlinTest)

val assertjVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
	implementation(kotlin("compiler-embeddable"))
	implementation(project(":detekt-rules"))
	implementation(project(":detekt-core"))
	implementation("org.assertj:assertj-core:$assertjVersion")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
