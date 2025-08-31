// Gradle test suite limitations mean we have to reference "functionalTest" in many places.
// https://github.com/gradle/gradle/issues/21285
@file:Suppress("StringLiteralDuplication")

import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("module")
    id("java-gradle-plugin")
    id("java-test-fixtures")
    id("idea")
    id("com.gradle.plugin-publish") version "1.3.1"
    // We use this published version of the detekt plugin to self analyse this project.
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.1"
    id("org.jetbrains.dokka") version "2.0.0"
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

repositories {
    mavenCentral()
    google()
}

group = "dev.detekt"
version = Versions.currentOrSnapshot()

nexusPublishing {
    repositories {
        create("sonatype") {
            nexusUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/")
            snapshotRepositoryUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
            username = providers.environmentVariable("ORG_GRADLE_PROJECT_SONATYPE_USERNAME")
            password = providers.environmentVariable("ORG_GRADLE_PROJECT_SONATYPE_PASSWORD")
        }
    }
}

detekt {
    source.from("src/functionalTest/kotlin")
    buildUponDefaultConfig = true
    baseline = file("config/gradle-plugin-baseline.xml")
    config.setFrom("config/gradle-plugin-detekt.yml")
}

dokka {
    dokkaPublications.configureEach {
        failOnWarning = true
    }

    dokkaSourceSets.configureEach {
        // Using `set` instead of simple property assignment to work around this Gradle 9 incompatibility: https://github.com/Kotlin/dokka/issues/4096
        apiVersion.set("1.4")
        modulePath = "detekt-gradle-plugin"

        externalDocumentationLinks {
            create("gradle") {
                url("https://docs.gradle.org/current/javadoc/")
                packageListUrl("https://docs.gradle.org/current/javadoc/element-list")
            }
        }
    }

    dokkaPublications.html {
        suppressInheritedMembers = true
    }
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
                implementation(project())
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
                        dependsOn("gradleMinVersionPluginUnderTestMetadata")
                    }
                }
            }
        }
    }
}

kotlin {
    @OptIn(ExperimentalBuildToolsApi::class, ExperimentalKotlinGradlePluginApi::class)
    compilerVersion = "2.1.21"

    compilerOptions {
        suppressWarnings = true
        // Note: Currently there are warnings for detekt-gradle-plugin that seemingly can't be fixed
        //       until Gradle releases an update (https://github.com/gradle/gradle/issues/16345)
        allWarningsAsErrors = false
        // The apiVersion Gradle property cannot be used here, so set api version using free compiler args.
        // https://youtrack.jetbrains.com/issue/KT-72247/KGP-Cannot-use-unsupported-API-version-with-compilerVersion-that-supports-it#focus=Comments-27-11050897.0-0
        freeCompilerArgs.addAll("-language-version", "1.8")
        freeCompilerArgs.addAll("-api-version", "1.7")
    }

    // Some functional tests reference internal functions in the Gradle plugin. This should become unnecessary as further
    // updates are made to the functional test suite.
    target.compilations.getByName("functionalTest") {
        associateWith(target.compilations.getByName("main"))
    }
}

val testKitRuntimeOnly by configurations.registering
val testKitGradleMinVersionRuntimeOnly by configurations.registering

dependencies {
    compileOnly(libs.android.gradleApi)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.kotlin.gradlePluginApi)
    implementation(libs.sarif4k)
    testFixturesCompileOnly(libs.jetbrains.annotations)

    testKitRuntimeOnly(libs.kotlin.gradle.plugin)
    testKitRuntimeOnly(libs.android.gradle.plugin)
    testKitGradleMinVersionRuntimeOnly(libs.kotlin.gradle.plugin) {
        attributes {
            // Set this value to the minimum Gradle version tested in testKitGradleMinVersionRuntimeOnly source set
            attribute(GradlePluginApiVersion.GRADLE_PLUGIN_API_VERSION_ATTRIBUTE, objects.named("7.6.3"))
        }
    }

    // We use this published version of the detekt-formatting to self analyse this project.
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
}

gradlePlugin {
    website = "https://detekt.dev"
    vcsUrl = "https://github.com/detekt/detekt"
    plugins {
        create("detektBasePlugin") {
            id = "dev.detekt.gradle.base"
            implementationClass = "dev.detekt.gradle.plugin.DetektBasePlugin"
        }
        create("detektPlugin") {
            id = "dev.detekt"
            implementationClass = "dev.detekt.gradle.plugin.DetektPlugin"
        }
        create("detektCompilerPlugin") {
            id = "dev.detekt.gradle.compiler-plugin"
            implementationClass = "dev.detekt.gradle.plugin.DetektKotlinCompilerPlugin"
        }
        configureEach {
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

signing {
    val signingKey = providers.gradleProperty("SIGNING_KEY").orNull
    val signingPwd = providers.gradleProperty("SIGNING_PWD").orNull
    if (signingKey.isNullOrBlank() || signingPwd.isNullOrBlank()) {
        logger.info("Signing disabled as the GPG key was not found")
    } else {
        logger.info("GPG Key found - Signing enabled")
    }

    useInMemoryPgpKeys(signingKey, signingPwd)
    sign(publishing.publications)
    isRequired = !(signingKey.isNullOrBlank() || signingPwd.isNullOrBlank())
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

    // Manually inject dependency to gradle-testkit since the default injected plugin classpath is from `main.runtime`.
    pluginUnderTestMetadata {
        pluginClasspath.from(testKitRuntimeOnly)
    }

    validatePlugins {
        enableStricterValidation = true
    }

    register<PluginUnderTestMetadata>("gradleMinVersionPluginUnderTestMetadata") {
        pluginClasspath.setFrom(sourceSets.main.get().output, testKitGradleMinVersionRuntimeOnly)
        outputDirectory = layout.buildDirectory.dir(name)
    }

    withType<Test>().configureEach {
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

dependencyAnalysis {
    issues {
        all {
            onAny {
                severity("fail")
            }
        }
    }
    structure {
        // Could potentially remove in future if DAGP starts handling this natively https://github.com/autonomousapps/dependency-analysis-gradle-plugin/issues/1269
        bundle("junit-jupiter") {
            includeDependency("org.junit.jupiter:junit-jupiter")
            includeDependency("org.junit.jupiter:junit-jupiter-api")
            includeDependency("org.junit.jupiter:junit-jupiter-params")
        }
    }
}
