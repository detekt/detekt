import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
}

configurations.testImplementation.get().extendsFrom(configurations.kotlinTest.get())

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
