import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.Verify

val kotlinVersion: String = libs.versions.kotlin.get()
val detektVersion: String = Versions.DETEKT

val kotlinCompilerChecksum: String by project

group = "io.github.detekt"
version = "$kotlinVersion-$detektVersion"

val detektPublication = "DetektPublication"

plugins {
    id("module")
    alias(libs.plugins.gradleVersions)
    alias(libs.plugins.shadow)
    alias(libs.plugins.download)
}

dependencies {
    compileOnly(libs.kotlin.compilerEmbeddable)

    implementation(projects.detektApi)
    implementation(projects.detektTooling)
    runtimeOnly(projects.detektCore)
    runtimeOnly(projects.detektRules)

    testImplementation(libs.assertj)
    testImplementation(libs.kotlinCompileTesting)
    testImplementation(libs.kotlin.compilerEmbeddable)
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

tasks.shadowJar.configure {
    relocate("org.jetbrains.kotlin.com.intellij", "com.intellij")
    relocate("org.snakeyaml.engine", "dev.detekt.shaded.snakeyaml")
    relocate("io.github.davidburstrom.contester", "dev.detekt.shaded.contester")
    mergeServiceFiles()
    dependencies {
        include(dependency("io.gitlab.arturbosch.detekt:.*"))
        include(dependency("io.github.detekt:.*"))
        include(dependency("org.snakeyaml:snakeyaml-engine"))
        include(dependency("io.github.davidburstrom.contester:contester-breakpoint"))
    }
}

val verifyKotlinCompilerDownload by tasks.registering(Verify::class) {
    src(file("$rootDir/build/kotlinc/kotlin-compiler-$kotlinVersion.zip"))
    algorithm("SHA-256")
    checksum(kotlinCompilerChecksum)
    outputs.upToDateWhen { true }
}

val downloadKotlinCompiler by tasks.registering(Download::class) {
    src("https://github.com/JetBrains/kotlin/releases/download/v$kotlinVersion/kotlin-compiler-$kotlinVersion.zip")
    dest(file("$rootDir/build/kotlinc/kotlin-compiler-$kotlinVersion.zip"))
    overwrite(false)
    finalizedBy(verifyKotlinCompilerDownload)
}

val unzipKotlinCompiler by tasks.registering(Copy::class) {
    dependsOn(downloadKotlinCompiler)
    from(zipTree(downloadKotlinCompiler.get().dest))
    into(file("$rootDir/build/kotlinc/$kotlinVersion"))
}

val testPluginKotlinc by tasks.registering(Task::class) {
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
    val pluginParameters = "plugin:detekt-compiler-plugin:debug=true"

    val kotlincExecution = providers.exec {
        workingDir = outputDir.get().asFile
        workingDir.mkdirs()

        args = listOf(
            sourceFile.path,
            "-Xplugin=${tasks.shadowJar.get().archiveFile.get().asFile.absolutePath}",
            "-P",
        )

        if (org.apache.tools.ant.taskdefs.condition.Os.isFamily("windows")) {
            executable = "$baseExecutablePath.bat"
            args("\"$pluginParameters\"")
        } else {
            executable = baseExecutablePath
            args(pluginParameters)
        }
    }

    doLast {
        if (!kotlincExecution.standardError.asText.get().contains("warning: magicNumber:")) {
            throw GradleException(
                "kotlinc run with compiler plugin did not find MagicNumber issue as expected"
            )
        }
    }
}

tasks.check {
    dependsOn(testPluginKotlinc)
}
