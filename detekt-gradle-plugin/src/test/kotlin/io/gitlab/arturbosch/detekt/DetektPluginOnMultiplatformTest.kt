package io.gitlab.arturbosch.detekt

import com.android.build.gradle.AppExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.style.specification.describe

object DetektPluginOnMultiplatformTest : Spek({

    describe("multiplatform projects - Common target") {

        it("creates detekt tasks for the Common target") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    // We need to provide at least one target
                    jvm()
                }

                evaluate()

                assertThat(getTask("check").dependencies())
                    .contains("detektMetadataMain")

                getTask("detektBaselineMetadataMain")
            }
        }

        it("configures detekt correctly") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    // We need to provide at least one target
                    jvm()
                }

                configureExtension<DetektExtension> {
                    reports.sarif.enabled = true
                }

                evaluate()

                assertThat((getTask("detektMetadataMain") as Detekt).reports.sarif.enabled).isTrue
            }
        }

        it("configures detekt without type resolution") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    // We need to provide at least one target
                    jvm()
                }

                evaluate()

                assertThat((getTask("detektMetadataMain") as Detekt).classpath).isEmpty()
            }
        }
    }

    describe("multiplatform projects - JVM target") {

        it("creates detekt tasks for the JVM target") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    jvm()
                }

                evaluate()

                assertThat(getTask("check").dependencies())
                    .contains("detektJvmMain", "detektJvmTest")

                getTask("detektBaselineJvmMain")
                getTask("detektBaselineJvmTest")
            }
        }

        it("creates detekt tasks for multiple JVM target") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    jvm("jvmBackend")
                    jvm("jvmEmbedded")
                }

                evaluate()

                assertThat(getTask("check").dependencies()).contains(
                    "detektJvmBackendMain",
                    "detektJvmBackendTest",
                    "detektJvmEmbeddedMain",
                    "detektJvmEmbeddedTest",
                )

                getTask("detektBaselineJvmBackendMain")
                getTask("detektBaselineJvmBackendTest")
                getTask("detektBaselineJvmEmbeddedMain")
                getTask("detektBaselineJvmEmbeddedTest")
            }
        }

        it("configures detekt correctly") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    jvm()
                }

                configureExtension<DetektExtension> {
                    reports.sarif.enabled = true
                }

                evaluate()

                assertThat((getTask("detektJvmMain") as Detekt).reports.sarif.enabled).isTrue
                assertThat((getTask("detektJvmTest") as Detekt).reports.sarif.enabled).isTrue
            }
        }

        it("configures detekt with type resolution") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    jvm()
                }

                // A repository is necessary otherwise Gradle will fail
                // to resolve the `classpath` property
                project.repositories.mavenCentral()

                evaluate()

                assertThat((getTask("detektJvmMain") as Detekt).classpath).isNotEmpty
                assertThat((getTask("detektJvmTest") as Detekt).classpath).isNotEmpty
            }
        }
    }

    describe(
        "multiplatform projects - Android target",
        skip = if (isAndroidSdkInstalled()) Skip.No else Skip.Yes("No android sdk.")) {

        it("creates detekt tasks for Android") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                    apply("com.android.application")
                }

                configureExtension<AppExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                }

                configureExtension<KotlinMultiplatformExtension> {
                    android()
                }

                evaluate()

                assertThat(getTask("check").dependencies())
                    .contains(
                        "detektAndroidDebug",
                        "detektAndroidDebugUnitTest",
                        "detektAndroidDebugAndroidTest",
                        "detektAndroidRelease",
                        "detektAndroidReleaseUnitTest",
                    )

                getTask("detektBaselineAndroidDebug")
                getTask("detektBaselineAndroidDebugUnitTest")
                getTask("detektBaselineAndroidDebugAndroidTest")
                getTask("detektBaselineAndroidRelease")
                getTask("detektBaselineAndroidReleaseUnitTest")
            }
        }

        it("configures detekt correctly") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                    apply("com.android.application")
                }

                configureExtension<AppExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                }

                configureExtension<KotlinMultiplatformExtension> {
                    android()
                }

                configureExtension<DetektExtension> {
                    reports.sarif.enabled = true
                }

                evaluate()

                assertThat((getTask("detektAndroidDebug") as Detekt).reports.sarif.enabled).isTrue
                assertThat((getTask("detektAndroidDebugUnitTest") as Detekt).reports.sarif.enabled).isTrue
                assertThat((getTask("detektAndroidDebugAndroidTest") as Detekt).reports.sarif.enabled).isTrue
                assertThat((getTask("detektAndroidRelease") as Detekt).reports.sarif.enabled).isTrue
                assertThat((getTask("detektAndroidReleaseUnitTest") as Detekt).reports.sarif.enabled).isTrue
            }
        }

        it("configures detekt with type resolution") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                    apply("com.android.application")
                }

                configureExtension<AppExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                }

                configureExtension<KotlinMultiplatformExtension> {
                    android()
                }

                // A repository is necessary otherwise Gradle will fail
                // to resolve the `classpath` property
                project.repositories.mavenCentral()

                evaluate()

                assertThat((getTask("detektAndroidDebug") as Detekt).classpath).isNotEmpty
                assertThat((getTask("detektAndroidRelease") as Detekt).classpath).isNotEmpty
            }
        }
    }

    describe("multiplatform projects - JS target") {

        it("creates detekt tasks for JS") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    js {
                        browser()
                    }
                }

                evaluate()

                assertThat(getTask("check").dependencies())
                    .contains(
                        "detektJsMain",
                        "detektJsTest",
                    )

                getTask("detektBaselineJsMain")
                getTask("detektBaselineJsTest")
            }
        }

        it("configures detekt correctly") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<DetektExtension> {
                    reports.sarif.enabled = true
                }

                configureExtension<KotlinMultiplatformExtension> {
                    js {
                        browser()
                    }
                }

                evaluate()

                assertThat((getTask("detektJsMain") as Detekt).reports.sarif.enabled).isTrue
                assertThat((getTask("detektJsTest") as Detekt).reports.sarif.enabled).isTrue
            }
        }

        it("configures detekt without type resolution") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    js {
                        browser()
                    }
                }

                evaluate()

                assertThat((getTask("detektJsMain") as Detekt).classpath).isEmpty()
                assertThat((getTask("detektJsTest") as Detekt).classpath).isEmpty()
            }
        }
    }

    describe("multiplatform projects - iOS target") {

        it("creates detekt tasks for iOS") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    ios()
                }

                evaluate()

                assertThat(getTask("check").dependencies())
                    .contains(
                        "detektIosArm64Main",
                        "detektIosArm64Test",
                        "detektIosX64Main",
                        "detektIosX64Test",
                    )

                getTask("detektBaselineIosArm64Main")
                getTask("detektBaselineIosArm64Test")
                getTask("detektBaselineIosX64Main")
                getTask("detektBaselineIosX64Test")
            }
        }

        it("configures detekt correctly") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<DetektExtension> {
                    reports.sarif.enabled = true
                }

                configureExtension<KotlinMultiplatformExtension> {
                    ios()
                }

                evaluate()

                assertThat((getTask("detektIosArm64Main") as Detekt).reports.sarif.enabled).isTrue
                assertThat((getTask("detektIosArm64Test") as Detekt).reports.sarif.enabled).isTrue
                assertThat((getTask("detektIosX64Main") as Detekt).reports.sarif.enabled).isTrue
                assertThat((getTask("detektIosX64Test") as Detekt).reports.sarif.enabled).isTrue
            }
        }

        it("configures detekt without type resolution") {
            with(ProjectBuilder.builder().build()) {
                with(pluginManager) {
                    apply(DetektPlugin::class.java)
                    apply("kotlin-multiplatform")
                }

                configureExtension<KotlinMultiplatformExtension> {
                    ios()
                }

                evaluate()

                assertThat((getTask("detektIosArm64Main") as Detekt).classpath).isEmpty()
                assertThat((getTask("detektIosArm64Test") as Detekt).classpath).isEmpty()
                assertThat((getTask("detektIosX64Main") as Detekt).classpath).isEmpty()
                assertThat((getTask("detektIosX64Test") as Detekt).classpath).isEmpty()
            }
        }
    }
})

internal const val ANDROID_COMPILE_SDK_VERSION = 29

internal fun Task.dependencies() = taskDependencies.getDependencies(this).map { it.name }

internal fun Project.getTask(name: String) = project.tasks.getAt(name)

internal fun Project.evaluate() = (this as ProjectInternal).evaluate()

internal inline fun <reified T : Any> Project.configureExtension(configuration: T.() -> Unit = {}) {
    project.extensions.findByType(T::class.java)?.apply(configuration)
}

/**
 * ANDROID_SDK_ROOT is preferred over ANDROID_HOME, but the check here is more lenient.
 * See [Android CLI Environment Variables](https://developer.android.com/studio/command-line/variables.html)
 */
internal fun isAndroidSdkInstalled() =
    System.getenv("ANDROID_SDK_ROOT") != null || System.getenv("ANDROID_HOME") != null
