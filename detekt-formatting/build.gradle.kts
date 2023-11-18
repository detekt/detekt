plugins {
    id("module")
}

val extraDepsToPackage: Configuration by configurations.creating
val testRuntimeOnlyPriority: Configuration = configurations.resolvable("testRuntimeOnlyPriority").get()
sourceSets.test.configure { runtimeClasspath = testRuntimeOnlyPriority + runtimeClasspath }

dependencies {
    compileOnly(projects.detektApi)
    implementation(libs.ktlintRulesetStandard) {
        exclude(group = "org.jetbrains.kotlin")
    }

    runtimeOnly(libs.slf4j.api)

    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)

    /* Workaround for https://youtrack.jetbrains.com/issue/KT-60813.
       Required due to detekt-main-kts embedding a ProGuarded version of SLF4J.
       This dependency will be placed at the beginning of the testRuntimeClasspath,
       so that it takes precedence over the ProGuarded version coming later from kotlin-test.
     */
    testRuntimeOnlyPriority(libs.slf4j.api)

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
