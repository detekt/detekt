import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
}

dependencies {
    implementation("org.yaml:snakeyaml:${Versions.SNAKEYAML}")
    api(kotlin("compiler-embeddable"))
    api(project(":detekt-psi-utils"))

    testImplementation(project(":detekt-parser"))
    testImplementation(project(":detekt-test"))
}

// bundle detekt's version to use it on runtime
tasks.withType<Jar>().configureEach {
    manifest {
        attributes(mapOf("DetektVersion" to Versions.DETEKT))
    }
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
