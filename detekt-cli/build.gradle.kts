application {
    mainClassName = "io.gitlab.arturbosch.detekt.cli.Main"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.beust:jcommander")
    implementation(project(":detekt-tooling"))
    implementation(project(":detekt-parser"))
    runtimeOnly(project(":detekt-core"))
    runtimeOnly(project(":detekt-rules"))

    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-rules"))
}

// Implements https://github.com/brianm/really-executable-jars-maven-plugin maven plugin behaviour.
// To check details how it works, see http://skife.org/java/unix/2011/06/20/really_executable_jars.html.
// Extracted from https://github.com/pinterest/ktlint/blob/a86d1c76c44d0a1c1adc3f756f36d8b4cac15d32/ktlint/build.gradle#L40-L57
val shadowJarExecutable by tasks.registering {
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
