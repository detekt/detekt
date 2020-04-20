application {
    mainClassName = "io.gitlab.arturbosch.detekt.cli.Main"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":detekt-core"))
    runtimeOnly(project(":detekt-rules"))
    implementation("com.beust:jcommander:${Versions.JCOMMANDER}")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${Versions.KOTLINX_HTML}") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-rules"))
}

// bundle detekt's version for debug logging on rule exceptions
tasks.withType<Jar> {
    manifest {
        attributes(mapOf("DetektVersion" to Versions.DETEKT))
    }
}

// Implements https://github.com/brianm/really-executable-jars-maven-plugin maven plugin behaviour.
// To check details how it works, see http://skife.org/java/unix/2011/06/20/really_executable_jars.html.
// Extracted from https://github.com/pinterest/ktlint/blob/a86d1c76c44d0a1c1adc3f756f36d8b4cac15d32/ktlint/build.gradle#L40-L57
tasks.register<DefaultTask>("shadowJarExecutable") {
    description = "Creates self-executable file, that runs generated shadow jar"
    group = "Distribution"

    inputs.files(tasks.named("shadowJar"))
    outputs.file("$buildDir/run/detekt")

    doLast {
        val execFile = outputs.files.singleFile
        execFile.outputStream().use {
            it.write("#!/bin/sh\n\nexec java -Xmx512m -jar \"\$0\" \"\$@\"\n\n".toByteArray())
            it.write(inputs.files.singleFile.readBytes())
        }
        execFile.setExecutable(true, false)
    }
}
