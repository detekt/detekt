plugins {
    module
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
}

repositories {
    mavenCentral()
    google()
}

val intTest: Configuration by configurations.creating

dependencies {
    implementation(libs.kotlin.gradlePluginApi)
    implementation("io.github.detekt.sarif4k:sarif4k")
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)

    testImplementation(projects.detektTestUtils)
    testImplementation(libs.kotlin.gradlePlugin)
    intTest(libs.kotlin.gradlePlugin)
    intTest(libs.android.gradlePlugin)
}

gradlePlugin {
    // hack to prevent building two jar's overwriting each other and leading to invalid signatures
    // when publishing the Gradle plugin, this property must be present
    isAutomatedPublishing = System.getProperty("automatePublishing")?.toBoolean() ?: false
    plugins {
        register("detektPlugin") {
            id = "io.gitlab.arturbosch.detekt"
            implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
        }
    }
}

// Manually inject dependency to gradle-testkit since the default injected plugin classpath is from `main.runtime`.
tasks.pluginUnderTestMetadata {
    pluginClasspath.from(intTest)
}

tasks.validatePlugins {
    enableStricterValidation.set(true)
}

pluginBundle {
    website = "https://detekt.github.io/detekt"
    vcsUrl = "https://github.com/detekt/detekt"
    description = "Static code analysis for Kotlin"
    tags = listOf("kotlin", "detekt", "code-analysis", "linter", "codesmells", "android")

    (plugins) {
        "detektPlugin" {
            id = "io.gitlab.arturbosch.detekt"
            displayName = "Static code analysis for Kotlin"
        }
    }
}

tasks {
    val writeDetektVersionProperties by registering(WriteProperties::class) {
        description = "Write the properties file with the Detekt version to be used by the plugin"
        encoding = "UTF-8"
        outputFile = file("$buildDir/versions.properties")
        property("detektVersion", project.version)
    }

    processResources {
        from(writeDetektVersionProperties)
    }

    processTestResources {
        from(writeDetektVersionProperties)
    }
}
