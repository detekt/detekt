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
    }

    describe(
        "detekt plugin - Android",
        skip = if (System.getenv("ANDROID_SDK_ROOT") != null) Skip.No else Skip.Yes("No android sdk.")
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
            }

            assertThat((project.getTask("detekt") as Detekt).parallel).isTrue()
        }

        it("creates experimental tasks if the Android library plugin is present") {
            with(ProjectBuilder.builder().build()) {
                androidPluginSetup("com.android.library")

                configureExtension<LibraryExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                }

                evaluate()

                assertThat(getTask("detektMain").dependencies())
                    .containsExactlyInAnyOrder("detektDebug", "detektRelease")

                assertThat(getTask("detektTest").dependencies())
                    .containsExactlyInAnyOrder(
                        "detektDebugUnitTest",
                        "detektReleaseUnitTest",
                        "detektDebugAndroidTest"
                    )
            }
        }

        it("creates experimental tasks if the Android application plugin is present") {
            with(ProjectBuilder.builder().build()) {

                androidPluginSetup("com.android.application")

                configureExtension<AppExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                }

                evaluate()

                assertThat(getTask("detektMain").dependencies())
                    .containsExactlyInAnyOrder("detektDebug", "detektRelease")

                assertThat(getTask("detektTest").dependencies())
                    .containsExactlyInAnyOrder(
                        "detektDebugUnitTest",
                        "detektReleaseUnitTest",
                        "detektDebugAndroidTest"
                    )
            }
        }

        it("creates experimental tasks if the Android test plugin is present") {
            with(ProjectBuilder.builder().build()) {
                ProjectBuilder.builder().withParent(this).withName("prod").build()

                androidPluginSetup("com.android.test")

                configureExtension<TestExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                    targetProjectPath("prod")
                }

                evaluate()

                // Test extensions by default have only the debug variant configured
                assertThat(getTask("detektMain").dependencies())
                    .containsExactlyInAnyOrder("detektDebug")

                // There is no test code for test modules, so we don't create a task for that either
                assertThat(tasks.findByName("detektTest")).isNull()
            }
        }

        it("creates experimental tasks for different build variants") {
            with(ProjectBuilder.builder().build()) {

                androidPluginSetup("com.android.library")

                configureExtension<LibraryExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                    flavorTestSetup()
                }

                evaluate()

                assertThat(getTask("detektMain").dependencies())
                    .containsExactlyInAnyOrder(
                        "detektYoungHarryDebug",
                        "detektYoungHarryRelease",
                        "detektOldHarryDebug",
                        "detektOldHarryRelease",
                        "detektYoungSallyDebug",
                        "detektYoungSallyRelease",
                        "detektOldSallyDebug",
                        "detektOldSallyRelease"
                    )

                assertThat(getTask("detektTest").dependencies())
                    .containsExactlyInAnyOrder(
                        // unit tests
                        "detektYoungHarryDebugUnitTest",
                        "detektYoungHarryReleaseUnitTest",
                        "detektOldHarryDebugUnitTest",
                        "detektOldHarryReleaseUnitTest",
                        "detektYoungSallyDebugUnitTest",
                        "detektYoungSallyReleaseUnitTest",
                        "detektOldSallyDebugUnitTest",
                        "detektOldSallyReleaseUnitTest",
                        // instrumentation tests
                        "detektYoungHarryDebugAndroidTest",
                        "detektOldHarryDebugAndroidTest",
                        "detektYoungSallyDebugAndroidTest",
                        "detektOldSallyDebugAndroidTest"
                    )
            }
        }

        it("creates experimental tasks for different build variants excluding ignored variants") {
            with(ProjectBuilder.builder().build()) {

                androidPluginSetup("com.android.library")

                configureExtension<LibraryExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                    flavorTestSetup()
                }

                configureExtension<DetektExtension> {
                    ignoredVariants = listOf("youngHarryDebug", "oldSallyRelease")
                }

                evaluate()

                assertThat(getTask("detektMain").dependencies())
                    .containsExactlyInAnyOrder(
                        "detektYoungHarryRelease",
                        "detektOldHarryDebug",
                        "detektOldHarryRelease",
                        "detektYoungSallyDebug",
                        "detektYoungSallyRelease",
                        "detektOldSallyDebug"
                    )

                assertThat(getTask("detektTest").dependencies())
                    .containsExactlyInAnyOrder(
                        // unit tests
                        "detektYoungHarryReleaseUnitTest",
                        "detektOldHarryDebugUnitTest",
                        "detektOldHarryReleaseUnitTest",
                        "detektYoungSallyDebugUnitTest",
                        "detektYoungSallyReleaseUnitTest",
                        "detektOldSallyDebugUnitTest",
                        // instrumentation tests
                        "detektOldHarryDebugAndroidTest",
                        "detektYoungSallyDebugAndroidTest",
                        "detektOldSallyDebugAndroidTest"
                    )
            }
        }

        it("creates experimental tasks for different build variants excluding ignored build types") {
            with(ProjectBuilder.builder().build()) {

                androidPluginSetup("com.android.library")

                configureExtension<LibraryExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                    flavorTestSetup()
                }

                configureExtension<DetektExtension> {
                    ignoredBuildTypes = listOf("release")
                }

                evaluate()

                assertThat(getTask("detektMain").dependencies())
                    .containsExactlyInAnyOrder(
                        "detektYoungHarryDebug",
                        "detektOldHarryDebug",
                        "detektYoungSallyDebug",
                        "detektOldSallyDebug"
                    )

                assertThat(getTask("detektTest").dependencies())
                    .containsExactlyInAnyOrder(
                        // unit tests
                        "detektYoungHarryDebugUnitTest",
                        "detektOldHarryDebugUnitTest",
                        "detektYoungSallyDebugUnitTest",
                        "detektOldSallyDebugUnitTest",
                        // instrumentation tests
                        "detektYoungHarryDebugAndroidTest",
                        "detektOldHarryDebugAndroidTest",
                        "detektYoungSallyDebugAndroidTest",
                        "detektOldSallyDebugAndroidTest"
                    )
            }
        }

        it("creates no tasks if all build types are ignored") {
            with(ProjectBuilder.builder().build()) {

                androidPluginSetup("com.android.library")

                configureExtension<LibraryExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                    flavorTestSetup()
                }

                configureExtension<DetektExtension> {
                    ignoredBuildTypes = listOf("debug", "release")
                }

                evaluate()

                assertThat(tasks.findByName("detektMain")).isNull()
                assertThat(tasks.findByName("detektTest")).isNull()
            }
        }

        it("creates experimental tasks for different build variants excluding ignored flavors") {
            with(ProjectBuilder.builder().build()) {

                androidPluginSetup("com.android.library")

                configureExtension<LibraryExtension> {
                    compileSdkVersion(ANDROID_COMPILE_SDK_VERSION)
                    flavorTestSetup()
                }

                configureExtension<DetektExtension> {
                    ignoredFlavors = listOf("oldHarry", "youngSally")
                }

                evaluate()

                assertThat(getTask("detektMain").dependencies())
                    .containsExactlyInAnyOrder(
                        "detektYoungHarryDebug",
                        "detektYoungHarryRelease",
                        "detektOldSallyDebug",
                        "detektOldSallyRelease"
                    )

                assertThat(getTask("detektTest").dependencies())
                    .containsExactlyInAnyOrder(
                        // unit tests
                        "detektYoungHarryDebugUnitTest",
                        "detektYoungHarryReleaseUnitTest",
                        "detektOldSallyDebugUnitTest",
                        "detektOldSallyReleaseUnitTest",
                        // instrumentation tests
                        "detektYoungHarryDebugAndroidTest",
                        "detektOldSallyDebugAndroidTest"
                    )
            }
        }
    }
})

internal const val ANDROID_COMPILE_SDK_VERSION = 29

internal fun Task.dependencies() = taskDependencies.getDependencies(this).map { it.name }

internal fun Project.androidPluginSetup(androidPlugin: String) {
    with(pluginManager) {
        apply(DetektPlugin::class.java)
        apply(androidPlugin)
        apply("kotlin-android")
    }
}

internal fun Project.getTask(name: String) = project.tasks.getAt(name)

internal fun Project.evaluate() = (this as ProjectInternal).evaluate()

internal fun BaseExtension.flavorTestSetup() {
    flavorDimensions("age", "name")
    productFlavors {
        it.create("harry").apply {
            dimension = "name"
        }
        it.create("sally").apply {
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
