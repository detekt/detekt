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

    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj)

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
        inputs.files(shadowJar)
        outputs.upToDateWhen { true }
        classpath = files(shadowJar)
        args = listOf("--help")
    }

    val runWithArgsFile by registering(JavaExec::class) {
        // The task generating these jar files run first.
        inputs.files(shadowJar, formattingJar)
        // This task does not adopt incremental-build (up-to-date) check because it is reading
        // the entire directory as the input source.
        outputs.upToDateWhen { false }
        classpath = files(shadowJar)
        workingDir = rootDir
        args = listOf("@./config/detekt/argsfile", "-p", formattingJar.singleFile.path)
    }

    check {
        dependsOn(runWithHelpFlag, runWithArgsFile)
    }
}
