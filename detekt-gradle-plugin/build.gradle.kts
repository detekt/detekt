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
    id("detekt-internal")
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
                implementation(testFixtures(project()))
            }

            targets {
                all {
                    testTask.configure {
                        // If `androidSdkInstalled` is false, skip running DetektAndroidSpec
                        val isAndroidSdkInstalled = providers.environmentVariable("ANDROID_SDK_ROOT").isPresent ||
                            providers.environmentVariable("ANDROID_HOME").isPresent
                        inputs.property("isAndroidSdkInstalled", isAndroidSdkInstalled).optional(true)

                        // Manually add all project runtime dependencies. This repo is referenced from functional tests.
                        setOf(
                            "detekt-api",
                            "detekt-core",
                            "detekt-cli",
                            "detekt-metrics",
                            "detekt-parser",
                            "detekt-psi-utils",
                            "detekt-report-html",
                            "detekt-report-md",
                            "detekt-report-sarif",
                            "detekt-report-txt",
                            "detekt-report-xml",
                            "detekt-rules",
                            "detekt-rules-complexity",
                            "detekt-rules-coroutines",
                            "detekt-rules-documentation",
                            "detekt-rules-empty",
                            "detekt-rules-errorprone",
                            "detekt-rules-exceptions",
                            "detekt-rules-naming",
                            "detekt-rules-performance",
                            "detekt-rules-style",
                            "detekt-tooling",
                            "detekt-utils",
                        ).forEach { projectName ->
                            dependsOn(":$projectName:publishIvyPublicationToGradlePluginFunctionalTestRepository")
                        }

                        environment(
                            "DGP_PROJECT_DEPS_REPO_PATH",
                            layout.buildDirectory.dir("repo").get().asFile.invariantSeparatorsPath
                        )
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
    compileOnly(projects.detektCli)

    testKitRuntimeOnly(libs.kotlin.gradle)
    testKitJava17RuntimeOnly(libs.android.gradle.maxSupported)

    detektPlugins(projects.detektFormatting)
}

gradlePlugin {
    website = "https://detekt.dev"
    vcsUrl = "https://github.com/detekt/detekt"
    plugins {
        create("detektPlugin") {
            id = "io.gitlab.arturbosch.detekt"
            implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
            displayName = "Static code analysis for Kotlin"
            description = "Static code analysis for Kotlin"
            tags = listOf("kotlin", "detekt", "code-analysis", "linter", "codesmells", "android")
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
            tags = listOf("kotlin", "detekt", "code-analysis", "linter", "codesmells", "android")
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
    retry {
        @Suppress("MagicNumber")
        if (providers.environmentVariable("CI").isPresent) {
            maxRetries = 2
            maxFailures = 20
        }
    }
}
