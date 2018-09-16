import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

application {
	mainClassName = "io.gitlab.arturbosch.detekt.watcher.MainKt"
}

configurations.compile.extendsFrom(configurations.kotlinImplementation)
configurations.testImplementation.extendsFrom(configurations.kotlinTest)

val jcommanderVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
	implementation("com.beust:jcommander:$jcommanderVersion")
	implementation("io.gitlab.arturbosch:ksh:0.1.1")
	implementation(project(":detekt-cli"))
	implementation(project(":detekt-core"))
	testImplementation(project(":detekt-test"))
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}

tasks.withType<ShadowJar> {
	mergeServiceFiles()
}
