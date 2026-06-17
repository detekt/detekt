plugins {
    id("module")
}

val extraDepsToPackage = configurations.register("extraDepsToPackage")
val ktlintToBundle = configurations.register("ktlintToBundle")

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)
    // compileOnly so ktlint-repackage is not listed in the published POM (it's bundled into the JAR)
    compileOnly(projects.detektRulesKtlintWrapper.ktlintRepackage) {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, named(Bundling.SHADOWED))
        }
    }
    ktlintToBundle(projects.detektRulesKtlintWrapper.ktlintRepackage) {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, named(Bundling.SHADOWED))
        }
    }

    runtimeOnly(libs.slf4j.api)

    testImplementation(projects.detektRulesKtlintWrapper.ktlintRepackage) {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, named(Bundling.SHADOWED))
        }
    }
    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektApi)
    testRuntimeOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    testImplementation(libs.classgraph)

    testRuntimeOnly(libs.slf4j.nop)
    testCompileOnly(libs.jetbrains.annotations)

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
    dependsOn(ktlintToBundle, extraDepsToPackage)
    from(
        ktlintToBundle.get().map { zipTree(it) },
        extraDepsToPackage.get().map { zipTree(it) },
    )
}
