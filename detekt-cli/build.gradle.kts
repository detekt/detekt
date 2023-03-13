import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    alias(libs.plugins.shadow)
    id("module")
    application
}

application {
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
}

val pluginsJar: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    implementation(libs.jcommander)
    implementation(projects.detektTooling)
    implementation(projects.detektParser)
    implementation(libs.kotlin.compilerEmbeddable) {
        version {
            strictly(libs.versions.kotlin.get())
        }
    }
    runtimeOnly(projects.detektCore)
    runtimeOnly(projects.detektRules)

    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj)

    pluginsJar(projects.detektFormatting)
    pluginsJar(projects.detektRulesLibraries)
    pluginsJar(projects.detektRulesRuleauthors)
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

    shadowDistZip {
        archiveBaseName.set("detekt-cli")
    }
    shadowDistTar { enabled = false }
    distZip { enabled = false }
    distTar { enabled = false }

    processTestResources {
        filter(ReplaceTokens::class, "tokens" to mapOf("kotlinVersion" to libs.versions.kotlin.get()))
        filteringCharset = "UTF-8"
    }

    val runWithHelpFlag by registering(JavaExec::class) {
        outputs.upToDateWhen { true }
        classpath = files(shadowJar)
        args = listOf("--help")
    }

    val runWithArgsFile by registering(JavaExec::class) {
        // The task generating these jar files run first.
        inputs.files(pluginsJar)
        doNotTrackState("The entire root directory is read as the input source.")
        classpath = files(shadowJar)
        workingDir = rootDir
        args = listOf("@./config/detekt/argsfile", "-p", pluginsJar.files.joinToString(",") { it.path })
    }

    withType<Jar>().configureEach {
        manifest {
            // Workaround for https://github.com/detekt/detekt/issues/5576
            attributes(mapOf("Add-Opens" to "java.base/java.lang"))
        }
    }

    check {
        dependsOn(runWithHelpFlag, runWithArgsFile)
    }
}
