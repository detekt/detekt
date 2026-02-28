package dev.detekt.gradle.plugin.internal

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektPlugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

internal object DetektAndroidCompilations {

    private const val VERIFICATION_GROUP = "verification"
    private const val TYPE_RESOLUTION_SUFFIX = "all variants with type resolution"

    fun registerTasks(project: Project, extension: DetektExtension) {
        project.extensions.getByType(
            KotlinAndroidExtension::class.java
        ).target.compilations.configureEach { compilation ->
            project.registerJvmCompilationDetektTask(extension, compilation)
            project.registerJvmCompilationCreateBaselineTask(extension, compilation)
            project.registerJvmCompilationProfilingTask(extension, compilation)
        }
    }

    private fun DetektExtension.matchesIgnoredConfiguration(variant: Variant): Boolean =
        ignoredVariants.get().contains(variant.name) ||
            ignoredBuildTypes.get().contains(variant.buildType) ||
            ignoredFlavors.get().contains(variant.flavorName)

    @Suppress("LongMethod")
    fun linkTasks(project: Project, extension: DetektExtension) {
        val mainTaskProvider =
            project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}Main") {
                it.group = VERIFICATION_GROUP
                it.description = "Run detekt analysis for production classes across $TYPE_RESOLUTION_SUFFIX"
            }

        val testTaskProvider =
            project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}Test") {
                it.group = VERIFICATION_GROUP
                it.description = "Run detekt analysis for test classes across $TYPE_RESOLUTION_SUFFIX"
            }

        val mainBaselineTaskProvider =
            project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}Main") {
                it.group = VERIFICATION_GROUP
                it.description = "Creates detekt baseline files for production classes across $TYPE_RESOLUTION_SUFFIX"
            }

        val testBaselineTaskProvider =
            project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}Test") {
                it.group = VERIFICATION_GROUP
                it.description = "Creates detekt baseline files for test classes across $TYPE_RESOLUTION_SUFFIX"
            }

        val mainProfilingTaskProvider =
            project.tasks.register("${DetektPlugin.PROFILING_TASK_NAME}Main") {
                it.group = VERIFICATION_GROUP
                it.description = "Run detekt profiling for production classes across $TYPE_RESOLUTION_SUFFIX"
            }

        val testProfilingTaskProvider =
            project.tasks.register("${DetektPlugin.PROFILING_TASK_NAME}Test") {
                it.group = VERIFICATION_GROUP
                it.description = "Run detekt profiling for test classes across $TYPE_RESOLUTION_SUFFIX"
            }

        project.extensions.findByType(AndroidComponentsExtension::class.java)?.let { componentsExtension ->
            componentsExtension.onVariants { variant ->
                if (!extension.matchesIgnoredConfiguration(variant)) {
                    mainTaskProvider.configure {
                        it.dependsOn(DetektPlugin.DETEKT_TASK_NAME + variant.name.capitalize())
                    }
                    mainBaselineTaskProvider.configure {
                        it.dependsOn(DetektPlugin.BASELINE_TASK_NAME + variant.name.capitalize())
                    }
                    mainProfilingTaskProvider.configure {
                        it.dependsOn(DetektPlugin.PROFILING_TASK_NAME + variant.name.capitalize())
                    }
                    variant.nestedComponents.forEach { testVariant ->
                        testTaskProvider.configure {
                            it.dependsOn(DetektPlugin.DETEKT_TASK_NAME + testVariant.name.capitalize())
                        }
                        testBaselineTaskProvider.configure {
                            it.dependsOn(DetektPlugin.BASELINE_TASK_NAME + testVariant.name.capitalize())
                        }
                        testProfilingTaskProvider.configure {
                            it.dependsOn(DetektPlugin.PROFILING_TASK_NAME + testVariant.name.capitalize())
                        }
                    }
                }
            }
        }
    }
}
