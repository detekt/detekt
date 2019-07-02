import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files.createDirectories
import java.nio.file.Files.newOutputStream
import java.util.Comparator.comparing
import java.util.Properties as JavaProperties

plugins {
    id("org.jetbrains.dokka")
}

configurations.testImplementation.extendsFrom(configurations["kotlinTest"])

val yamlVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
    implementation("org.yaml:snakeyaml:$yamlVersion")
    api(kotlin("compiler-embeddable"))
    implementation(kotlin("reflect"))

    testImplementation(project(":detekt-test"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}

tasks.named<KotlinCompile>("compileKotlin") {
    val detektProperties = JavaProperties()
    detektProperties.setProperty("version", project.property("detektVersion").toString())

    inputs.property("detektProperties", detektProperties.toSortedMap(comparing { it.toString() }))

    doLast {
        val propertiesFilePath = destinationDir.resolve("META-INF/detekt.properties").toPath().toAbsolutePath()
        propertiesFilePath.parent?.let { createDirectories(it) }
        newOutputStream(propertiesFilePath).use { outputStream ->
            detektProperties.store(outputStream, null)
        }
    }
}

tasks.withType<DokkaTask> {
    // suppresses undocumented classes but not dokka warnings https://github.com/Kotlin/dokka/issues/90
    reportUndocumented = false
    outputFormat = "jekyll"
    outputDirectory = "$rootDir/docs/pages/kdoc"
    @Suppress("MagicNumber")
    jdkVersion = 8
}
