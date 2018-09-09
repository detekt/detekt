application {
	mainClassName = "io.gitlab.arturbosch.detekt.watchservice.MainKt"
}

configurations.compile.extendsFrom(configurations.kotlinImplementation)
configurations.testImplementation.extendsFrom(configurations.kotlinTest)

val jcommanderVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
	implementation("com.beust:jcommander:$jcommanderVersion")
	implementation(project(":detekt-cli"))
	implementation(project(":detekt-core"))
	testImplementation(project(":detekt-test"))
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
