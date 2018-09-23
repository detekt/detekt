import java.util.concurrent.Callable

configurations.testImplementation.extendsFrom(configurations.kotlinTest)
configurations.compile.isTransitive = false

val ktlintVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
	implementation(kotlin("compiler-embeddable"))
	compileOnly(project(":detekt-api"))
	compile("com.github.shyiko.ktlint:ktlint-ruleset-standard:$ktlintVersion") {
		exclude(group = "org.jetbrains.kotlin")
	}
	compile("com.github.shyiko.ktlint:ktlint-core:$ktlintVersion") {
		exclude(group = "org.jetbrains.kotlin")
	}

	testCompile(project(":detekt-api"))
	testCompile(project(":detekt-test"))
	testCompile(project(":detekt-core"))
	testRuntime("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntime("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}

tasks.withType<Jar> {
	from(Callable {
		configurations.compile.map {
			if (it.isDirectory) it else zipTree(it)
		}
	})
}
