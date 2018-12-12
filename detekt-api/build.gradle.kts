configurations.testImplementation.extendsFrom(configurations["kotlinTest"])

val yamlVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
	implementation("org.yaml:snakeyaml:$yamlVersion")
	api(kotlin("compiler-embeddable"))
	implementation(kotlin("reflect"))

	testImplementation(project(":detekt-test"))

	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
