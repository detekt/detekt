application {
	mainClassName = "io.gitlab.arturbosch.detekt.cli.Main"
}

val kotlinVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project
val jcommanderVersion: String by project
val detektVersion: String by project

// implementation.extendsFrom kotlin is not enough for using cli in a gradle task - #58
configurations.testImplementation.extendsFrom(configurations.kotlinTest)

dependencies {
	implementation(project(":detekt-core"))
	implementation(project(":detekt-rules"))
	implementation("com.beust:jcommander:$jcommanderVersion")
	implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")

	testImplementation(project(":detekt-test"))
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}

tasks {
	"test" {
		dependsOn(":detekt-generator:generateDocumentation")
	}
}

// bundle detekt's version for debug logging on rule exceptions
tasks.withType<Jar> {
	manifest {
		attributes(mapOf("DetektVersion" to detektVersion))
	}
}
