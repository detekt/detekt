import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
}

val yamlVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
    implementation("org.yaml:snakeyaml:$yamlVersion")
    api(kotlin("compiler-embeddable"))
    implementation(kotlin("reflect"))
    testImplementation(project(":detekt-test"))
}

tasks.withType<DokkaTask>().configureEach {
    outputFormat = "jekyll"
    outputDirectory = "$rootDir/docs/pages/kdoc"
    configuration {
        // suppresses undocumented classes but not dokka warnings https://github.com/Kotlin/dokka/issues/90
        reportUndocumented = false
        @Suppress("MagicNumber")
        jdkVersion = 8
    }
}

tasks.withType<Test>().configureEach {
    systemProperty("kotlinVersion", embeddedKotlinVersion)

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
