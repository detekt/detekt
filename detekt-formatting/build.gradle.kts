import java.util.concurrent.Callable

configurations.testImplementation.extendsFrom(configurations["kotlinTest"])
configurations["compile"].isTransitive = false

val ktlintVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
    implementation(kotlin("compiler-embeddable"))
    implementation(project(":detekt-api"))
    implementation("com.github.shyiko.ktlint:ktlint-ruleset-standard:$ktlintVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.github.shyiko.ktlint:ktlint-core:$ktlintVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-core"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}

tasks.withType<Jar> {
    from(Callable {
        configurations["compile"].map {
            if (it.isDirectory) it else zipTree(it)
        }
    })
}
