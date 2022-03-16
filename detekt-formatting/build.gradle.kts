plugins {
    id("module")
}

dependencies {
    implementation(projects.detektApi)
    implementation(libs.ktlint.rulesetStandard) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation(libs.ktlint.core) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation(libs.ktlint.rulesetExperimental) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation(libs.ktlint.microutilsKotlinLoggingJvm)

    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
}

tasks.build { finalizedBy(":detekt-generator:generateDocumentation") }

val depsToPackage = setOf(
    "org.ec4j.core",
    "com.pinterest.ktlint",
    "com.pinterest.ktlint",
    "io.github.microutils",
    "org.slf4j",
)

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // allow duplicates
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { dependency -> depsToPackage.any { it in dependency.toString() } }
            .map { if (it.isDirectory) it else zipTree(it) }
    })
}
