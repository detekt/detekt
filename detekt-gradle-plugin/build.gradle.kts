plugins {
    module
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.12.0"
}

repositories {
    google()
}

sourceSets {
    main {
        java.srcDir("$buildDir/generated/src")
    }

    create("intTest") {
        java.srcDirs("src/intTest/kotlin", "$buildDir/generated/src")
        compileClasspath += sourceSets.main.get().output + configurations["intTestCompileClasspath"]
        runtimeClasspath += sourceSets.main.get().output + configurations["intTestRuntimeClasspath"]
    }
}

val intTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

configurations["intTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    val androidGradlePlugin = "com.android.tools.build:gradle:4.1.1"
    implementation(kotlin("gradle-plugin-api"))
    compileOnly(androidGradlePlugin)

    testImplementation(project(":detekt-test-utils"))
    testImplementation(kotlin("gradle-plugin"))
    testImplementation(androidGradlePlugin)

    constraints {
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.0") {
            because("""Android Gradle Plugin 4.1.1 depends on Kotlin 1.3.72 but we should not mix 1.3 and 1.4.
                This constraint should be lifted on Android Gradle Plugin 4.2.0. See
                https://dl.google.com/android/maven2/com/android/tools/build/gradle/4.2.0-beta02/gradle-4.2.0-beta02.pom
            """)
        }
    }
}

val intTest by tasks.registering(Test::class) {
    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    shouldRunAfter(tasks.test)
}

tasks.check {
    dependsOn(intTest)
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
    testSourceSets(sourceSets["intTest"])
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

val generateDefaultDetektVersionFile by tasks.registering {
    val name = "PluginVersion.kt"
    val defaultDetektVersionFile = File("$buildDir/generated/src/io/gitlab/arturbosch/detekt", name)

    inputs.property(name, project.version)
    outputs.file(defaultDetektVersionFile)

    doFirst {
        defaultDetektVersionFile.parentFile.mkdirs()
        defaultDetektVersionFile.writeText("""
            package io.gitlab.arturbosch.detekt

            internal const val DEFAULT_DETEKT_VERSION = "$version"

            """.trimIndent()
        )
    }
}

tasks.compileKotlin {
    dependsOn(generateDefaultDetektVersionFile)
}
