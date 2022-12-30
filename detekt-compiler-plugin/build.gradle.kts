import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.Verify
import java.io.ByteArrayOutputStream

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

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("compiler-embeddable"))

    implementation(projects.detektApi)
    implementation(projects.detektTooling)
    runtimeOnly(projects.detektCore)
    runtimeOnly(projects.detektRules)

    testImplementation(libs.assertj)
    testImplementation(libs.kotlinCompileTesting)
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

tasks.test {
    // https://github.com/detekt/detekt/issues/5646
    enabled = false
}

tasks.shadowJar.configure {
    relocate("org.jetbrains.kotlin.com.intellij", "com.intellij")
    mergeServiceFiles()
    dependencies {
        include(dependency("io.gitlab.arturbosch.detekt:.*"))
        include(dependency("io.github.detekt:.*"))
        include(dependency("org.yaml:snakeyaml"))
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

val testPluginKotlinc by tasks.registering(RunTestExecutable::class) {
    dependsOn(unzipKotlinCompiler, tasks.shadowJar)

    args(
        listOf(
            "$rootDir/src/test/resources/hello.kt",
            "-Xplugin=${tasks.shadowJar.get().archiveFile.get().asFile.absolutePath}",
            "-P",
        )
    )

    val baseExecutablePath = "${unzipKotlinCompiler.get().destinationDir}/kotlinc/bin/kotlinc"
    val pluginParameters = "plugin:detekt-compiler-plugin:debug=true"

    if (org.apache.tools.ant.taskdefs.condition.Os.isFamily("windows")) {
        executable(file("$baseExecutablePath.bat"))
        args("\"$pluginParameters\"")
    } else {
        executable(file(baseExecutablePath))
        args(pluginParameters)
    }

    errorOutput = ByteArrayOutputStream()
    // dummy path - required for RunTestExecutable task but doesn't do anything
    outputDir = file("$buildDir/tmp/kotlinc")

    doLast {
        if (!errorOutput.toString().contains("warning: magicNumber:")) {
            throw GradleException(
                "kotlinc $kotlinVersion run with compiler plugin did not find MagicNumber issue as expected"
            )
        }
        (this as RunTestExecutable).executionResult.get().assertNormalExitValue()
    }
}
