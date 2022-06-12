import org.gradle.api.internal.classpath.ModuleRegistry
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    id("module")
    `java-gradle-plugin`
    `java-test-fixtures`
    idea
    signing
    alias(libs.plugins.pluginPublishing)
    // We use this published version of the Detekt plugin to self analyse this project.
    id("io.gitlab.arturbosch.detekt") version "1.20.0"
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
    config = files("config/gradle-plugin-detekt.yml")
}

testing {
    suites {
        getByName("test", JvmTestSuite::class) {
            dependencies {
                implementation(libs.assertj)
                implementation(libs.kotlin.gradle)
                implementation(gradleKotlinDsl())

                // See https://github.com/gradle/gradle/issues/16774#issuecomment-853407822
                runtimeOnly(
                    files(
                        serviceOf<ModuleRegistry>()
                            .getModule("gradle-tooling-api-builders")
                            .classpath
                            .asFiles
                            .first()
                    )
                )
            }
            targets {
                all {
                    testTask.configure {
                        inputs.property("androidSdkRoot", System.getenv("ANDROID_SDK_ROOT")).optional(true)
                        inputs.property("androidHome", System.getenv("ANDROID_HOME")).optional(true)
                    }
                }
            }
        }
        register("functionalTest", JvmTestSuite::class) {
            useJUnitJupiter(libs.versions.junit.get())

            dependencies {
                implementation(libs.assertj)
            }
        }
    }
}

val pluginCompileOnly: Configuration by configurations.creating
val functionalTestImplementation: Configuration by configurations.getting

configurations.compileOnly { extendsFrom(pluginCompileOnly) }

dependencies {
    compileOnly(libs.kotlin.gradlePluginApi)
    implementation(libs.sarif4k)

    // Migrate to `implementation(testFixtures(project))` in test suite configuration when this issue is fixed:
    // https://github.com/gradle/gradle/pull/19472
    functionalTestImplementation(testFixtures(project))

    pluginCompileOnly(libs.android.gradle)
    pluginCompileOnly(libs.kotlin.gradle)

    // We use this published version of the detekt-formatting to self analyse this project.
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.20.0")
}

gradlePlugin {
    plugins {
        register("detektPlugin") {
            id = "io.gitlab.arturbosch.detekt"
            implementationClass = "io.gitlab.arturbosch.detekt.DetektPlugin"
        }
    }
    // Source sets that require the Gradle TestKit dependency
    testSourceSets(
        sourceSets["testFixtures"],
        sourceSets["functionalTest"],
    )
}

// Some functional tests reference internal functions in the Gradle plugin. This should become unnecessary as further
// updates are made to the functional test suite.
kotlin.target.compilations.getByName("functionalTest") {
    associateWith(target.compilations.getByName("main"))
}

// Manually inject dependency to gradle-testkit since the default injected plugin classpath is from `main.runtime`.
tasks.pluginUnderTestMetadata {
    pluginClasspath.from(pluginCompileOnly)
}

tasks.validatePlugins {
    enableStricterValidation.set(true)
}

pluginBundle {
    website = "https://detekt.dev"
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

    check {
        dependsOn(testing.suites.named("functionalTest"))
    }

    ideaModule {
        notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/13480")
    }
}

// Skip publishing of test fixture API & runtime variants
with(components["java"] as AdhocComponentWithVariants) {
    withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
    withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
}

tasks.withType<Sign>().configureEach {
    notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/13470")
}

val signingKey = "SIGNING_KEY".byProperty
val signingPwd = "SIGNING_PWD".byProperty
if (signingKey.isNullOrBlank() || signingPwd.isNullOrBlank()) {
    logger.info("Signing disabled as the GPG key was not found")
} else {
    logger.info("GPG Key found - Signing enabled")
    afterEvaluate {
        signing {
            useInMemoryPgpKeys(signingKey, signingPwd)
            publishing.publications.forEach(::sign)
        }
    }
}

afterEvaluate {
    publishing {
        publications.filterIsInstance<MavenPublication>().forEach {
            it.pom {
                description.set("The official Detekt Gradle Plugin")
                name.set("detekt-gradle-plugin")
                url.set("https://detekt.dev")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("Detekt Developers")
                        name.set("Detekt Developers")
                        email.set("info@detekt.dev")
                    }
                }
                scm {
                    url.set("https://github.com/detekt/detekt")
                }
            }
        }
    }
}

val String.byProperty: String? get() = findProperty(this) as? String
