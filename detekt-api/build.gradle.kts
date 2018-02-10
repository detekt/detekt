configurations.implementation.extendsFrom(configurations.kotlinImplementation)
configurations.testImplementation.extendsFrom(configurations.kotlinTest)

val yamlVersion by project
val junitPlatformVersion by project
val spekVersion by project

dependencies {
	implementation("org.yaml:snakeyaml:$yamlVersion")

	testImplementation(project(":detekt-test"))

	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-console:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
