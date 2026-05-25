plugins {
    id("module")
}

val extraDepsToPackage = configurations.register("extraDepsToPackage")

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)
    implementation(projects.detektRulesKtlintWrapper.ktlintRepackage) {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, named(Bundling.SHADOWED))
        }
    }

    runtimeOnly(libs.slf4j.api)

    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    testImplementation(libs.classgraph)

    testRuntimeOnly(libs.slf4j.nop)
    extraDepsToPackage(libs.slf4j.nop)
}

consumeGeneratedConfig(
    fromProject = projects.detektGenerator,
    fromConfiguration = "generatedKtlintWrapperConfig",
    forTask = tasks.sourcesJar
)
consumeGeneratedConfig(
    fromProject = projects.detektGenerator,
    fromConfiguration = "generatedKtlintWrapperConfig",
    forTask = tasks.processResources
)

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // allow duplicates
    dependsOn(configurations.runtimeClasspath, extraDepsToPackage)
    from(
        configurations.runtimeClasspath.get()
            .filter { it.toString().contains("ktlint-repackage") }
            .map { zipTree(it) },
        extraDepsToPackage.get().map { zipTree(it) },
    )
}
