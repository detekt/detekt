import org.gradle.api.internal.classpath.ModuleRegistry
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    id("module")
    `java-gradle-plugin`
    `java-test-fixtures`
    idea
    alias(libs.plugins.pluginPublishing)
}

detekt {
    source.from("src/functionalTest/kotlin")
}

repositories {
    mavenCentral()
    google()
}

testing {
    suites {
        getByName("test", JvmTestSuite::class) {
            dependencies {
                implementation(libs.assertj)
                implementation(libs.kotlin.gradle)
                implementation(gradleKotlinDsl())

                // Workaround for gradle/gradle#16774, see
                // https://github.com/gradle/gradle/issues/16774#issuecomment-853407822
                // This should be reviewed and dropped if fixed as planned in Gradle 7.5
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
    implementation(projects.detektUtils)

    // Migrate to `implementation(testFixtures(project))` in test suite configuration when Gradle 7.5 released
    // (https://github.com/gradle/gradle/pull/19472)
    functionalTestImplementation(testFixtures(project))

    pluginCompileOnly(libs.android.gradle)
    pluginCompileOnly(libs.kotlin.gradle)
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
    // Source sets that require the Gradle TestKit dependency
    testSourceSets(
        sourceSets["testFixtures"],
        sourceSets["functionalTest"],
        sourceSets["test"]
    )
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
