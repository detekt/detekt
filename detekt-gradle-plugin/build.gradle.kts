plugins {
    module
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.14.0"
}

repositories {
    mavenCentral()
    google()
    jcenter {
        content {
            includeModule("org.jetbrains.trove4j", "trove4j")
            includeModule("org.jetbrains.kotlinx", "kotlinx-html-jvm")
        }
    }
}

val intTest: Configuration by configurations.creating

dependencies {
    val androidGradlePlugin = "com.android.tools.build:gradle:4.1.3"
    implementation(kotlin("gradle-plugin-api"))
    implementation("io.github.detekt.sarif4k:sarif4k")
    compileOnly(androidGradlePlugin)
    compileOnly(kotlin("gradle-plugin"))

    testImplementation(project(":detekt-test-utils"))
    testImplementation(kotlin("gradle-plugin"))
    intTest(kotlin("gradle-plugin"))
    intTest(androidGradlePlugin)

    constraints {
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.32") {
            because(
                """Android Gradle Plugin 4.1.1 depends on Kotlin 1.3.72 but we should not mix 1.3 and 1.4.
                This constraint should be lifted on Android Gradle Plugin 4.2.0. See
                https://dl.google.com/android/maven2/com/android/tools/build/gradle/4.2.0-beta02/gradle-4.2.0-beta02.pom
            """
            )
        }
    }
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

tasks.processResources {
    filter<org.apache.tools.ant.filters.ReplaceTokens>(
        "tokens" to mapOf(
            "detektVersion" to project.version as String
        )
    )
}

tasks.processTestResources {
    filter<org.apache.tools.ant.filters.ReplaceTokens>(
        "tokens" to mapOf(
            "detektVersion" to project.version as String
        )
    )
}
