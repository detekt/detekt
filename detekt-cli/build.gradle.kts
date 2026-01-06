plugins {
    id("com.gradleup.shadow") version "9.3.1"
    id("module")
    id("application")
}

application {
    mainClass = "dev.detekt.cli.Main"
}

val pluginsJar by configurations.dependencyScope("pluginsJar") {
    isTransitive = false
}

val pluginsJarFiles by configurations.resolvable("pluginsJarFiles") {
    extendsFrom(pluginsJar)
}

dependencies {
    implementation(libs.jcommander)
    implementation(projects.detektApi)
    implementation(projects.detektTooling)
    implementation(projects.detektUtils)
    implementation(libs.kotlin.compiler) {
        version {
            strictly(libs.versions.kotlin.get())
        }
    }
    runtimeOnly(projects.detektCore)
    runtimeOnly(projects.detektRules)
    runtimeOnly(projects.detektReportHtml)
    runtimeOnly(projects.detektReportMarkdown)
    runtimeOnly(projects.detektReportSarif)
    runtimeOnly(projects.detektReportCheckstyle)

    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(projects.detektRulesKtlintWrapper)

    pluginsJar(projects.detektRulesKtlintWrapper)
    pluginsJar(projects.detektRulesLibraries)
    pluginsJar(projects.detektRulesRuleauthors)
}

shadow {
    addShadowVariantIntoJavaComponent = false
}

publishing {
    publications.named<MavenPublication>(DETEKT_PUBLICATION) {
        artifact(tasks.shadowJar)
    }
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        mergeServiceFiles()
        filesMatching("META-INF/services/**") {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    shadowDistZip {
        archiveBaseName = "detekt-cli"
    }
    shadowDistTar { enabled = false }
    distZip { enabled = false }
    distTar { enabled = false }

    val runWithHelpFlag by registering(JavaExec::class) {
        outputs.upToDateWhen { true }
        classpath = files(shadowJar)
        args = listOf("--help")
    }

    val runWithArgsFile by registering(JavaExec::class) {
        // The task generating these jar files run first.
        inputs.files(pluginsJarFiles)
        doNotTrackState("The entire root directory is read as the input source.")
        classpath = files(shadowJar)
        workingDir = rootDir
        args = listOf("@./config/detekt/argsfile", "-p", pluginsJarFiles.files.joinToString(",") { it.path })
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

val shadowDist: Configuration by configurations.consumable("shadowDist")
artifacts.add(shadowDist.name, tasks.shadowDistZip)
