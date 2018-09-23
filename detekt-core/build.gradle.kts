configurations.testImplementation.extendsFrom(configurations.kotlinTest)

val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
	implementation(kotlin("compiler-embeddable"))
	api(project(":detekt-api"))

	testImplementation(project(":detekt-rules"))
	testImplementation(project(":detekt-test"))
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
