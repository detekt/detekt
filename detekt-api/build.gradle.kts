import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
}

dependencies {
    implementation("org.yaml:snakeyaml:${Versions.SNAKEYAML}")
    api(kotlin("compiler-embeddable"))
    api(project(":detekt-psi-utils"))

    testImplementation(project(":detekt-core"))
    testImplementation(project(":detekt-test"))
}

tasks.withType<DokkaTask>().configureEach {
    outputFormat = "jekyll"
    outputDirectory = "$rootDir/docs/pages/kdoc"
    configuration {
        moduleName = project.name
        reportUndocumented = false
        @Suppress("MagicNumber")
        jdkVersion = 8
    }
}
