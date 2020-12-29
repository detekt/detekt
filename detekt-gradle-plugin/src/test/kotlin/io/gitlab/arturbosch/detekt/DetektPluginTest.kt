package io.gitlab.arturbosch.detekt

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.testfixtures.ProjectBuilder
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.style.specification.describe
import java.util.function.Predicate

object DetektPluginTest : Spek({
    describe("detekt plugin - JVM") {

        it("lazily adds detekt as a dependency of the `check` task") {
            val project = ProjectBuilder.builder().build()

            /* Ordering here is important - to prove lazily adding the dependency works the LifecycleBasePlugin must be
             * added to the project after the detekt plugin. */
            project.pluginManager.apply(DetektPlugin::class.java)
            project.pluginManager.apply(LifecycleBasePlugin::class.java)

            assertThat(project.tasks.getAt("check").dependencies()).contains("detekt")
        }

        it("configures detekt task correctly") {
            val project = ProjectBuilder.builder().build()

            with(project.pluginManager) {
                apply(DetektPlugin::class.java)
                apply(LifecycleBasePlugin::class.java)
                apply("org.jetbrains.kotlin.jvm")
            }

            project.configureExtension<DetektExtension> {
                reports {
                    it.sarif {
                        enabled = true
                    }
                }
            }
            project.evaluate()

            assertThat((project.getTask("detekt") as Detekt).reports.sarif.enabled).isTrue
            assertThat((project.getTask("detektMain") as Detekt).reports.sarif.enabled).isTrue
        }
    }

    describe(
        "detekt plugin - Android",
        skip = if (isAndroidSdkInstalled()) Skip.No else Skip.Yes("No android sdk.")
    ) {
        it("applies the base gradle plugin and creates a regular detekt task") {
            val project = ProjectBuilder.builder().build()

            with(project.pluginManager) {
                apply(DetektPlugin::class.java)
                apply(LifecycleBasePlugin::class.java)
            }

            assertThat(project.getTask("check").dependencies()).contains("detekt")
        }

        it("lets the base gradle plugin use it's configuration") {
            val project = ProjectBuilder.builder().build()

            with(project.pluginManager) {
                apply(DetektPlugin::class.java)
                apply(LifecycleBasePlugin::class.java)
            }

            project.configureExtension<DetektExtension> {
                parallel = true
                reports {
                    it.sarif {
                        enabled = true
                    }
                }
            }

            assertThat((project.getTask("detekt") as Detekt).parallel).isTrue
            assertThat((project.getTask("detekt") as Detekt).reports.sarif.enabled).isTrue
        }

        it("creates experimental tasks if the Android library plugin is present") {
            with(ProjectBuilder.builder().build()) {
                setupAndroidAndEvaluate<LibraryExtension>(
                    androidPluginName = "com.android.library",
                    configureBaseExtension = { compileSdkVersion(ANDROID_COMPILE_SDK_VERSION) },
                    configureDetektExtension = { reports { it.sarif.enabled = true } }
                )
                assertThat(getTask("detektAll").dependencies()).containsExactlyInAnyOrder(
                    "detektMain",
                    "detektDebug",
                    "detektRelease",
                    "detektTest",
                    "detektTestDebug",
                    "detektTestRelease",
                    "detektAndroidTest",
                    "detektAndroidTestDebug",
                    "detektAndroidTestRelease"
                )
                assertThat((getTask("detektDebug") as Detekt).reports.sarif.enabled).isTrue
            }
        }

        it("creates experimental tasks if the Android application plugin is present") {
            with(ProjectBuilder.builder().build()) {
                setupAndroidAndEvaluate<AppExtension>(
                    androidPluginName = "com.android.application",
                    configureBaseExtension = { compileSdkVersion(ANDROID_COMPILE_SDK_VERSION) },
                    configureDetektExtension = { reports { it.sarif.enabled = true } }
                )
                assertThat(getTask("detektAll").dependencies()).containsExactlyInAnyOrder(
                    "detektMain",
                    "detektDebug",
                    "detektRelease",
                    "detektTest",
                    "detektTestDebug",
                    "detektTestRelease",
                    "detektAndroidTest",
                    "detektAndroidTestDebug",
                    "detektAndroidTestRelease"
                )
                assertThat((getTask("detektDebug") as Detekt).reports.sarif.enabled).isTrue
            }
        }

        it("creates experimental tasks if the Android test plugin is present") {
            with(ProjectBuilder.builder().build()) {
                ProjectBuilder.builder().withParent(this).withName("prod").build()
                setupAndroidAndEvaluate<TestExtension>(
                    androidPluginName = "com.android.test",
                    configureBaseExtension = {
                        compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                        targetProjectPath("prod")
                    },
                    configureDetektExtension = { reports { it.sarif.enabled = true } }
                )
                // Test extensions by default have only the debug variant configured
                assertThat(getTask("detektAll").dependencies()).containsExactlyInAnyOrder(
                    "detektMain",
                    "detektDebug"
                )
                assertThat((getTask("detektDebug") as Detekt).reports.sarif.enabled).isTrue
            }
        }

        it("creates experimental tasks for different build variants") {
            with(ProjectBuilder.builder().build()) {

                setupAndroidAndEvaluate<LibraryExtension>(
                    androidPluginName = "com.android.library",
                    configureBaseExtension = {
                        compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                        setupFlavor()
                    },
                    configureDetektExtension = { }
                )

                assertThat(getTask("detektAll").dependencies()).containsExactlyInAnyOrder(
                    "detektAndroidTest",
                    "detektAndroidTestDebug",
                    "detektAndroidTestHarry",
                    "detektAndroidTestOld",
                    "detektAndroidTestOldHarry",
                    "detektAndroidTestOldHarryDebug",
                    "detektAndroidTestYoung",
                    "detektAndroidTestYoungHarry",
                    "detektAndroidTestYoungHarryDebug",
                    "detektDebug",
                    "detektHarry",
                    "detektMain",
                    "detektOld",
                    "detektOldHarry",
                    "detektOldHarryDebug",
                    "detektOldHarryRelease",
                    "detektRelease",
                    "detektTest",
                    "detektTestDebug",
                    "detektTestHarry",
                    "detektTestOld",
                    "detektTestOldHarry",
                    "detektTestOldHarryDebug",
                    "detektTestOldHarryRelease",
                    "detektTestRelease",
                    "detektTestYoung",
                    "detektTestYoungHarry",
                    "detektTestYoungHarryDebug",
                    "detektTestYoungHarryRelease",
                    "detektYoung",
                    "detektYoungHarry",
                    "detektYoungHarryDebug",
                    "detektYoungHarryRelease",
                    "detektAndroidTestRelease",
                )
            }
        }

        it("creates experimental tasks for different build variants excluding ignored variants") {
            with(ProjectBuilder.builder().build()) {

                setupAndroidAndEvaluate<LibraryExtension>(
                    androidPluginName = "com.android.library",
                    configureBaseExtension = {
                        compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                        setupFlavor()
                    },
                    configureDetektExtension = {
                        sourceSetFilter = Predicate { !it.contains("sally", ignoreCase = true) }
                    }
                )

                assertThat(getTask("detektAll").dependencies()).containsExactlyInAnyOrder(
                    "detektAndroidTest",
                    "detektAndroidTestDebug",
                    "detektAndroidTestHarry",
                    "detektAndroidTestOld",
                    "detektAndroidTestOldHarry",
                    "detektAndroidTestOldHarryDebug",
                    "detektAndroidTestRelease",
                    "detektAndroidTestYoung",
                    "detektAndroidTestYoungHarry",
                    "detektAndroidTestYoungHarryDebug",
                    "detektDebug",
                    "detektMain",
                    "detektOld",
                    "detektOldHarry",
                    "detektOldHarryDebug",
                    "detektOldHarryRelease",
                    "detektRelease",
                    "detektTest",
                    "detektTestDebug",
                    "detektTestHarry",
                    "detektTestOld",
                    "detektTestOldHarry",
                    "detektTestOldHarryDebug",
                    "detektTestOldHarryRelease",
                    "detektTestRelease",
                    "detektTestYoung",
                    "detektTestYoungHarry",
                    "detektTestYoungHarryDebug",
                    "detektTestYoungHarryRelease",
                    "detektYoung",
                    "detektYoungHarry",
                    "detektYoungHarryDebug",
                    "detektYoungHarryRelease",
                    "detektHarry",
                )
            }
        }
    }
})

internal const val ANDROID_COMPILE_SDK_VERSION = 29

internal fun Task.dependencies() = taskDependencies.getDependencies(this).map { it.name }

internal inline fun <reified T : BaseExtension> Project.setupAndroidAndEvaluate(
    androidPluginName: String,
    configureBaseExtension: T.() -> Unit,
    configureDetektExtension: DetektExtension.() -> Unit,
) {
    with(pluginManager) {
        apply(DetektPlugin::class.java)
        apply(androidPluginName)
        apply("kotlin-android")
    }

    configureExtension(configureBaseExtension)
    configureExtension(configureDetektExtension)
    evaluate()
}

internal fun Project.getTask(name: String) = project.tasks.getAt(name)

internal fun Project.evaluate() = (this as ProjectInternal).evaluate()

internal fun BaseExtension.setupFlavor() {
    flavorDimensions("age", "name")
    productFlavors {
        it.create("harry").apply {
            dimension = "name"
        }
        it.create("young").apply {
            dimension = "age"
        }
        it.create("old").apply {
            dimension = "age"
        }
    }
}

internal inline fun <reified T : Any> Project.configureExtension(configuration: T.() -> Unit = {}) {
    project.extensions.findByType(T::class.java)?.apply(configuration)
}

/**
 * ANDROID_SDK_ROOT is preferred over ANDROID_HOME, but the check here is more lenient.
 * See [Android CLI Environment Variables](https://developer.android.com/studio/command-line/variables.html)
 */
private fun isAndroidSdkInstalled() =
    System.getenv("ANDROID_SDK_ROOT") != null || System.getenv("ANDROID_HOME") != null
