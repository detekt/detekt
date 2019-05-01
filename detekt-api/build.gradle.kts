import org.jetbrains.dokka.gradle.DokkaTask

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

tasks.withType<DokkaTask> {
    // suppresses undocumented classes but not dokka warnings https://github.com/Kotlin/dokka/issues/90
    reportUndocumented = false
    outputFormat = "jekyll"
    outputDirectory = "$rootDir/docs/pages/kdoc"
    @Suppress("MagicNumber")
    jdkVersion = 8
}
