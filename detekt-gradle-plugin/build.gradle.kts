// Gradle test suite limitations mean we have to reference "functionalTest" in many places.
// https://github.com/gradle/gradle/issues/21285
@file:Suppress("StringLiteralDuplication")

import com.gradle.enterprise.gradleplugin.testretry.retry
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("module")
    `java-gradle-plugin`
    `java-test-fixtures`
    idea
    alias(libs.plugins.pluginPublishing)
    // We use this published version of the detekt plugin to self analyse this project.
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

repositories {
    mavenCentral()
    google()
}

group = "io.gitlab.arturbosch.detekt"
version = Versions.currentOrSnapshot()

detekt {
    source.from("src/functionalTest/kotlin")
    buildUponDefaultConfig = true
    baseline = file("config/gradle-plugin-baseline.xml")
    config.setFrom("config/gradle-plugin-detekt.yml")
}

testing {
    suites {
        getByName("test", JvmTestSuite::class) {
            dependencies {
                implementation(libs.assertj)
                implementation(libs.kotlin.gradle)
                implementation(gradleKotlinDsl())
            }
        }
        register("functionalTest", JvmTestSuite::class) {
            useJUnitJupiter(libs.versions.junit.get())

            dependencies {
                compileOnly("org.jetbrains:annotations:24.0.1")
                implementation(libs.assertj)
                implementation(testFixtures(project(":")))
            }

            targets {
                all {
                    testTask.configure {
                        // If `androidSdkInstalled` is false, skip running DetektAndroidSpec
                        val isAndroidSdkInstalled = System.getenv("ANDROID_SDK_ROOT") != null ||
                            System.getenv("ANDROID_HOME") != null
                        inputs.property("isAndroidSdkInstalled", isAndroidSdkInstalled).optional(true)
                    }
                }
            }
        }
    }
}

val testKitRuntimeOnly: Configuration by configurations.creating
val testKitJava17RuntimeOnly: Configuration by configurations.creating

dependencies {
    compileOnly(libs.android.gradle.minSupported)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.kotlin.gradlePluginApi)
    testFixturesCompileOnly("org.jetbrains:annotations:24.0.1")
    implementation(libs.sarif4k) {
        exclude("org.jetbrains.kotlin")
    }
    compileOnly("io.gitlab.arturbosch.detekt:detekt-cli:1.22.0")

    testKitRuntimeOnly(libs.kotlin.gradle)
    testKitJava17RuntimeOnly(libs.android.gradle.maxSupported)

    // We use this published version of the detekt-formatting to self analyse this project.
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
}

gradlePlugin {
    website.set("https://detekt.dev")
    vcsUrl.set("https://github.com/detekt/detekt")
    plugins {
        create("detektPlugin") {
            id = "io.gitlab.arturbosch.detekt"
            implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
            displayName = "Static code analysis for Kotlin"
            description = "Static code analysis for Kotlin"
            tags.set(listOf("kotlin", "detekt", "code-analysis", "linter", "codesmells", "android"))
        }
    }
    // Source sets that require the Gradle TestKit dependency
    testSourceSets(
        sourceSets["testFixtures"],
        sourceSets["functionalTest"],
    )
}

gradlePlugin {
    plugins {
        create("detektCompilerPlugin") {
            id = "io.github.detekt.gradle.compiler-plugin"
            implementationClass = "io.github.detekt.gradle.DetektKotlinCompilerPlugin"
            displayName = "Static code analysis for Kotlin"
            description = "Static code analysis for Kotlin"
            tags.set(listOf("kotlin", "detekt", "code-analysis", "linter", "codesmells", "android"))
        }
    }
}

// Some functional tests reference internal functions in the Gradle plugin. This should become unnecessary as further
// updates are made to the functional test suite.
kotlin.target.compilations.getByName("functionalTest") {
    associateWith(target.compilations.getByName("main"))
}

// Manually inject dependency to gradle-testkit since the default injected plugin classpath is from `main.runtime`.
tasks.pluginUnderTestMetadata {
    pluginClasspath.from(testKitRuntimeOnly)

    if (tasks.named<Test>("functionalTest").get().javaVersion.isCompatibleWith(JavaVersion.VERSION_17)) {
        pluginClasspath.from(testKitJava17RuntimeOnly)
    }
}

tasks.validatePlugins {
    enableStricterValidation.set(true)
}

tasks {
    val writeDetektVersionProperties by registering(WriteProperties::class) {
        description = "Write the properties file with the detekt version to be used by the plugin."
        encoding = "UTF-8"
        destinationFile.set(file("$buildDir/detekt-versions.properties"))
        property("detektVersion", project.version)
        property("detektCompilerPluginVersion", project.version)
    }

    processResources {
        from(writeDetektVersionProperties)
    }

    processTestResources {
        from(writeDetektVersionProperties)
    }

    check {
        dependsOn(testing.suites.named("functionalTest"))
    }

    ideaModule {
        notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/13480")
    }

    publishPlugins {
        notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/21283")
    }
}

// Skip publishing of test fixture API & runtime variants
with(components["java"] as AdhocComponentWithVariants) {
    withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
    withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        @Suppress("DEPRECATION")
        apiVersion.set(KotlinVersion.KOTLIN_1_4)
        freeCompilerArgs.add("-Xsuppress-version-warnings")
        // Note: Currently there are warnings for detekt-gradle-plugin that seemingly can't be fixed
        //       until Gradle releases an update (https://github.com/gradle/gradle/issues/16345)
        allWarningsAsErrors.set(false)
    }
}

tasks.withType<Test>().configureEach {
    retry {
        @Suppress("MagicNumber")
        if (System.getenv().containsKey("CI")) {
            maxRetries.set(2)
            maxFailures.set(20)
        }
    }
}
