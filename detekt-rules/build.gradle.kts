configurations.implementation.extendsFrom(configurations.kotlinImplementation)
configurations.testImplementation.extendsFrom(configurations.kotlinTest)

tasks {
	"build" {
		finalizedBy(":detekt-generator:generateDocumentation")
	}
}

val junitPlatformVersion by project
val spekVersion by project

dependencies {
	implementation(project(":detekt-api"))

	testImplementation(project(":detekt-test"))
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-console:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}

