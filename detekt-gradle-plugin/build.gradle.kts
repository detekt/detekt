// Gradle test suite limitations mean we have to reference "functionalTest" in many places.
// https://github.com/gradle/gradle/issues/21285
@file:Suppress("StringLiteralDuplication")

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.net.URI

plugins {
    id("module")
    id("java-gradle-plugin")
    id("java-test-fixtures")
    id("idea")
    id("com.gradle.plugin-publish") version "1.3.0"
    // We use this published version of the detekt plugin to self analyse this project.
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.16.3"
    id("org.jetbrains.dokka") version "1.9.20"
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

kotlin {
    @OptIn(ExperimentalBuildToolsApi::class, ExperimentalKotlinGradlePluginApi::class)
    compilerVersion = "2.0.10"
}

testing {
    suites {
        getByName("test", JvmTestSuite::class) {
            dependencies {
                implementation(libs.assertj.core)
                implementation(libs.kotlin.gradle.plugin)
                implementation(gradleKotlinDsl())
            }
        }
        register<JvmTestSuite>("functionalTest") {
            dependencies {
                implementation(libs.assertj.core)
                implementation(testFixtures(project()))
            }

            targets {
                all {
                    testTask.configure {
                        // If `androidSdkInstalled` is false, skip running DetektAndroidSpec
                        val isAndroidSdkInstalled = providers.environmentVariable("ANDROID_SDK_ROOT").isPresent ||
                            providers.environmentVariable("ANDROID_HOME").isPresent
                        inputs.property("isAndroidSdkInstalled", isAndroidSdkInstalled).optional(true)
                    }
                }
            }
        }
        register<JvmTestSuite>("functionalTestMinSupportedGradle") {
            dependencies {
                implementation(libs.assertj.core)
                implementation(testFixtures(project()))
            }
            targets {
                all {
                    testTask {
                        dependsOn(gradleMinVersionPluginUnderTestMetadata)
                    }
                }
            }
        }
    }
}

val testKitRuntimeOnly: Configuration by configurations.creating
val testKitJava17RuntimeOnly: Configuration by configurations.creating
val testKitGradleMinVersionRuntimeOnly: Configuration by configurations.creating

dependencies {
    compileOnly(libs.android.gradleApi)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.kotlin.gradlePluginApi)
    implementation(libs.sarif4k)
    testFixturesCompileOnly(libs.jetbrains.annotations)
    compileOnly(libs.jetbrains.annotations)

    testKitRuntimeOnly(libs.kotlin.gradle.plugin)
    testKitGradleMinVersionRuntimeOnly(libs.kotlin.gradle.plugin) {
        attributes {
            // Set this value to the minimum Gradle version tested in testKitGradleMinVersionRuntimeOnly source set
            attribute(GradlePluginApiVersion.GRADLE_PLUGIN_API_VERSION_ATTRIBUTE, objects.named("7.6.3"))
        }
    }
    testKitJava17RuntimeOnly(libs.android.gradle.plugin)

    // We use this published version of the detekt-formatting to self analyse this project.
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.7")
}

gradlePlugin {
    website = "https://detekt.dev"
    vcsUrl = "https://github.com/detekt/detekt"
    plugins {
        create("detektBasePlugin") {
            id = "io.github.detekt.gradle.base"
            implementationClass = "dev.detekt.gradle.plugin.DetektBasePlugin"
            displayName = "Static code analysis for Kotlin"
            description = "Static code analysis for Kotlin"
            tags = listOf("kotlin", "detekt", "code-analysis", "linter", "codesmells", "android")
        }
        create("detektPlugin") {
            id = "io.gitlab.arturbosch.detekt"
            implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
            displayName = "Static code analysis for Kotlin"
            description = "Static code analysis for Kotlin"
            tags = listOf("kotlin", "detekt", "code-analysis", "linter", "codesmells", "android")
        }
        create("detektCompilerPlugin") {
            id = "io.github.detekt.gradle.compiler-plugin"
            implementationClass = "io.github.detekt.gradle.DetektKotlinCompilerPlugin"
            displayName = "Static code analysis for Kotlin"
            description = "Static code analysis for Kotlin"
            tags = listOf("kotlin", "detekt", "code-analysis", "linter", "codesmells", "android")
        }
    }
    // Source sets that require the Gradle TestKit dependency
    testSourceSets(
        sourceSets["testFixtures"],
        sourceSets["functionalTest"],
        sourceSets["functionalTestMinSupportedGradle"],
    )
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

val gradleMinVersionPluginUnderTestMetadata by tasks.registering(PluginUnderTestMetadata::class) {
    pluginClasspath.setFrom(sourceSets.main.get().runtimeClasspath, testKitGradleMinVersionRuntimeOnly)
    outputDirectory = layout.buildDirectory.dir(name)
}

tasks.validatePlugins {
    enableStricterValidation = true
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

    processTestResources {
        from(writeDetektVersionProperties)
    }

    withType<DokkaTask>().configureEach {
        suppressInheritedMembers = true
        failOnWarning = true
        outputDirectory = layout.projectDirectory.dir("../website/static/kdoc/detekt-gradle-plugin")

        dokkaSourceSets.configureEach {
            apiVersion = "1.4"
            externalDocumentationLink {
                url = URI("https://docs.gradle.org/current/javadoc/").toURL()
            }
        }
    }

    check {
        dependsOn(
            testing.suites.named("functionalTest"),
            testing.suites.named("functionalTestMinSupportedGradle"),
        )
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

kotlin {
    compilerOptions {
        @Suppress("DEPRECATION")
        apiVersion = KotlinVersion.KOTLIN_1_4
        suppressWarnings = true
        // Note: Currently there are warnings for detekt-gradle-plugin that seemingly can't be fixed
        //       until Gradle releases an update (https://github.com/gradle/gradle/issues/16345)
        allWarningsAsErrors = false
    }
}

tasks.withType<Test>().configureEach {
    develocity {
        testRetry {
            @Suppress("MagicNumber")
            if (providers.environmentVariable("CI").isPresent) {
                maxRetries = 2
                maxFailures = 20
            }
        }
    }
}
