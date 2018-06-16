import java.util.concurrent.Callable

configurations.implementation.extendsFrom(configurations.kotlinImplementation)
configurations.testImplementation.extendsFrom(configurations.kotlinTest)
configurations.compile.isTransitive = false

val ktlintVersion by project
val junitPlatformVersion by project
val spekVersion by project

dependencies {
	compileOnly(project(":detekt-api"))
	compile("com.github.shyiko.ktlint:ktlint-ruleset-standard:$ktlintVersion") {
		exclude(group = "org.jetbrains.kotlin")
	}
	compile("com.github.shyiko.ktlint:ktlint-core:$ktlintVersion") {
		exclude(group = "org.jetbrains.kotlin")
	}

	testCompile(project(":detekt-api"))
	testCompile(project(":detekt-test"))
	testRuntime("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
	testRuntime("org.junit.platform:junit-platform-console:$junitPlatformVersion")
	testRuntime("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}

tasks.withType<Jar> {
	from(Callable {
		configurations.compile.map({
			if (it.isDirectory) it else zipTree(it)
		})
	})
}

