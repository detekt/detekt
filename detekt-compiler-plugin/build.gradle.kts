import de.undercouch.gradle.tasks.download.Download

val kotlinVersion: String = libs.versions.kotlin.get()
val detektVersion: String = Versions.DETEKT

version = "$kotlinVersion-$detektVersion"

plugins {
    id("module")
    id("com.gradleup.shadow") version "9.3.0"
    id("de.undercouch.download") version "5.6.0"
}

kotlin {
    compilerOptions {
        optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
    }
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTooling)

    compileOnly(libs.kotlin.compiler)

    runtimeOnly(projects.detektCore)
    runtimeOnly(projects.detektRules)

    testImplementation(libs.assertj.core)
    testImplementation(libs.kctfork.core)
}

shadow {
    addShadowVariantIntoJavaComponent = false
}

publishing {
    publications.named<MavenPublication>(DETEKT_PUBLICATION) {
        artifact(tasks.shadowJar)
    }
}

tasks.shadowJar {
    relocate("org.snakeyaml.engine", "dev.detekt.shaded.snakeyaml")
    mergeServiceFiles()
    dependencies {
        include(dependency("dev.detekt:.*"))
        include(dependency("org.snakeyaml:snakeyaml-engine"))
    }
}

val downloadKotlinCompiler by tasks.registering(Download::class) {
    src("https://github.com/JetBrains/kotlin/releases/download/v$kotlinVersion/kotlin-compiler-$kotlinVersion.zip")
    dest(file("$rootDir/build/kotlinc/kotlin-compiler-$kotlinVersion.zip"))
    overwrite(false)
}

val unzipKotlinCompiler by tasks.registering(Copy::class) {
    dependsOn(downloadKotlinCompiler)
    from(zipTree(downloadKotlinCompiler.get().dest))
    into(file("$rootDir/build/kotlinc/$kotlinVersion"))
}

val testPluginKotlinc by tasks.registering(Task::class) {
    enabled = false

    val outputDir = layout.buildDirectory.dir("tmp/kotlinc")
    val sourceFile = file("src/test/resources/hello.kt")

    inputs.dir(unzipKotlinCompiler.map { it.destinationDir })
        .withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.file(tasks.shadowJar.map { it.archiveFile })
        .withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.file(sourceFile)
        .withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.dir(outputDir)

    val baseExecutablePath = "${unzipKotlinCompiler.get().destinationDir}/kotlinc/bin/kotlinc"

    val kotlincExecution = providers.exec {
        workingDir = outputDir.get().asFile
        workingDir.mkdirs()

        args = listOf(
            sourceFile.path,
            "-language-version",
            "1.9",
            "-Xplugin=${tasks.shadowJar.get().archiveFile.get().asFile.absolutePath}",
            "-P",
            "plugin:detekt-compiler-plugin:debug=true".toArg(),
            "-P",
            "plugin:detekt-compiler-plugin:useDefaultConfig=true".toArg(),
        )

        executable = if (org.apache.tools.ant.taskdefs.condition.Os.isFamily("windows")) {
            "$baseExecutablePath.bat"
        } else {
            baseExecutablePath
        }
    }

    doLast {
        val stdErrOutput = kotlincExecution.standardError.asText.get()
        if (!stdErrOutput.contains("warning: doubleMutabilityForCollection:")) {
            throw GradleException(
                """
                    kotlinc run with compiler plugin did not find DoubleMutabilityForCollection issue in output:
                    $stdErrOutput
                """.trimIndent()
            )
        }
    }
}

private fun String.toArg() = if (org.apache.tools.ant.taskdefs.condition.Os.isFamily("windows")) {
    "\"$this\""
} else {
    this
}

tasks.check {
    dependsOn(testPluginKotlinc)
}
