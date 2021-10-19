plugins {
    alias(libs.plugins.shadow)
    id("module")
    application
}

application {
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
}

val formattingJar by configurations.creating {
    isTransitive = false
}

dependencies {
    implementation(libs.jcommander)
    implementation(projects.detektTooling)
    implementation(projects.detektParser)
    runtimeOnly(projects.detektCore)
    runtimeOnly(projects.detektRules)

    testImplementation(projects.detektTest)
    testImplementation(libs.bundles.testImplementation)
    testRuntimeOnly(libs.spek.runner)

    formattingJar(projects.detektFormatting)
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

publishing {
    publications.named<MavenPublication>(DETEKT_PUBLICATION) {
        artifact(tasks.shadowJar)
    }
}

tasks {
    shadowJar {
        mergeServiceFiles()
    }

    val runWithHelpFlag by registering(JavaExec::class) {
        classpath = files(shadowJar)
        args = listOf("--help")
    }

    val runWithArgsFile by registering(JavaExec::class) {
        inputs.files(formattingJar) // ensures detekt-formatting JAR is built before this task is executed
        classpath = files(shadowJar)
        workingDir = rootDir
        args = listOf("@./config/detekt/argsfile", "-p", formattingJar.singleFile.path)
    }

    check {
        dependsOn(runWithHelpFlag, runWithArgsFile)
    }
}
