plugins {
    id("module")
}

val extraDepsToPackage: Configuration by configurations.creating

dependencies {
    compileOnly(projects.detektApi)
    implementation(libs.ktlintRulesetStandard) {
        exclude(group = "org.jetbrains.kotlin")
    }

    runtimeOnly(libs.slf4j.api)

    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)

    testRuntimeOnly(libs.slf4j.nop)
    extraDepsToPackage(libs.slf4j.nop)
}

consumeGeneratedConfig(
    fromProject = projects.detektGenerator,
    fromConfiguration = "generatedFormattingConfig",
    forTask = tasks.sourcesJar
)
consumeGeneratedConfig(
    fromProject = projects.detektGenerator,
    fromConfiguration = "generatedFormattingConfig",
    forTask = tasks.processResources
)

val depsToPackage = setOf(
    "org.ec4j.core",
    "com.pinterest.ktlint",
    "io.github.oshai",
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

tasks.test {
    if (javaVersion.isJava9Compatible) {
        jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    }
}
