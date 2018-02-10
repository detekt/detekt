application {
	mainClassName = "io.gitlab.arturbosch.detekt.watchservice.MainKt"
}

configurations.compile.extendsFrom(configurations.kotlinImplementation)
configurations.testImplementation.extendsFrom(configurations.kotlinTest)

val jcommanderVersion by project
val junitPlatformVersion by project
val spekVersion by project

dependencies {
	implementation("com.beust:jcommander:$jcommanderVersion")
	implementation(project(":detekt-cli"))
	implementation(project(":detekt-core"))
	testImplementation(project(":detekt-test"))
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-console:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
