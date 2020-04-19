import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
}

dependencies {
    implementation("org.yaml:snakeyaml:${Versions.SNAKEYAML}")
    api(kotlin("compiler-embeddable"))

    testImplementation(project(":detekt-test"))
}

tasks.withType<DokkaTask> {
    outputFormat = "jekyll"
    outputDirectory = "$rootDir/docs/pages/kdoc"
    configuration {
        // suppresses undocumented classes but not dokka warnings https://github.com/Kotlin/dokka/issues/90
        reportUndocumented = false
        @Suppress("MagicNumber")
        jdkVersion = 8
    }
}

tasks.withType<Test> {
    systemProperty("kotlinVersion", embeddedKotlinVersion)

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
