plugins {
    alias(libs.plugins.kotlin)
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    google()
}

version = "1"

sourceSets.main {
    java.setSrcDirs(listOf(layout.projectDirectory.dir("../detekt-gradle-plugin/src/main/kotlin")))
}

dependencies {
    compileOnly(libs.android.gradle.minSupported)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.kotlin.gradlePluginApi)
    compileOnly("io.gitlab.arturbosch.detekt:detekt-cli:1.23.1")
}

gradlePlugin {
    plugins {
        create("detektPlugin") {
            id = "detekt-internal"
            implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
        }
    }
}

tasks {
    val writeDetektVersionProperties by registering(WriteProperties::class) {
        description = "Write the properties file with the detekt version to be used by the plugin."
        encoding = "UTF-8"
        destinationFile = layout.buildDirectory.file("detekt-versions.properties")
        property("detektVersion", project.version)
        property("detektCompilerPluginVersion", project.version)
    }

    processResources {
        from(writeDetektVersionProperties)
    }
}
