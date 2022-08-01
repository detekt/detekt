plugins {
    id("module")
}

val extraDepsToPackage: Configuration by configurations.creating

dependencies {
    compileOnly(projects.detektApi)
    implementation(libs.ktlint.rulesetStandard) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation(libs.ktlint.core) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation(libs.ktlint.rulesetExperimental) {
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)

    testRuntimeOnly(libs.slf4j.nop)
    extraDepsToPackage(libs.slf4j.nop)
}

tasks.build { finalizedBy(":detekt-generator:generateDocumentation") }

val depsToPackage = setOf(
    "org.ec4j.core",
    "com.pinterest.ktlint",
    "io.github.microutils",
)

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // allow duplicates
    dependsOn(configurations.runtimeClasspath, extraDepsToPackage)
    from(
        configurations.runtimeClasspath.get()
            .filter { dependency -> depsToPackage.any { it in dependency.toString() } }
            .map { if (it.isDirectory) it else zipTree(it) },
        extraDepsToPackage.map { zipTree(it) },
    )
}
