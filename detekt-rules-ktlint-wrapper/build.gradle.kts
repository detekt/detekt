plugins {
    id("module")
    id("generator")
}

val extraDepsToPackage = configurations.register("extraDepsToPackage")
configurations.implementation {
    extendsFrom(extraDepsToPackage)
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)

    extraDepsToPackage(libs.ktlint.ruleEngine) {
        exclude(group = "org.jetbrains.kotlin")
    }
    extraDepsToPackage(libs.ktlint.rulesetStandard) {
        exclude(group = "org.jetbrains.kotlin")
    }
    extraDepsToPackage(libs.ec4j)

    runtimeOnly(libs.slf4j.api)

    implementation(libs.kotlin.analysisApiIntellijApiSurfaceComponents)
    implementation(libs.kotlin.analysisApiIntellijImplementationComponents)
    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    testImplementation(libs.classgraph)
    testImplementation(libs.snakeyaml.engine)

    testRuntimeOnly(libs.slf4j.nop)
    testCompileOnly(libs.jetbrains.annotations)

    extraDepsToPackage(libs.slf4j.nop)
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // allow duplicates
    dependsOn(configurations.runtimeClasspath, extraDepsToPackage)
    from(
        { configurations.runtimeClasspath.get().map { zipTree(it) } },
        { extraDepsToPackage.get().map { zipTree(it) } },
    )
}

tasks.named("generateConfig") {
    val ktlintVersion = libs.versions.ktlint
    inputs.property("ktlintVersion", ktlintVersion)
    doLast {
        outputs.files.asFileTree.forEach {
            if (it.isFile) {
                it.writeText(it.readText().replace("<ktlintVersion/>", ktlintVersion.get()))
            }
        }
    }
}
