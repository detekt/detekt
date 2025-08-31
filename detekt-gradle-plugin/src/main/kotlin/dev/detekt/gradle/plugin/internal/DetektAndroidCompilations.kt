package dev.detekt.gradle.plugin.internal

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektPlugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal object DetektAndroidCompilations {
    fun registerTasks(project: Project, extension: DetektExtension) {
        project.extensions.getByType(KotlinAndroidProjectExtension::class.java).target.compilations.all { compilation ->
            project.registerJvmCompilationDetektTask(extension, compilation)
            project.registerJvmCompilationCreateBaselineTask(extension, compilation)
        }
    }

    private fun DetektExtension.matchesIgnoredConfiguration(variant: Variant): Boolean =
        ignoredVariants.get().contains(variant.name) ||
            ignoredBuildTypes.get().contains(variant.buildType) ||
            ignoredFlavors.get().contains(variant.flavorName)

    fun linkTasks(project: Project, extension: DetektExtension) {
        val mainTaskProvider =
            project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}Main") {
                it.group = "verification"
                it.description = "Run detekt analysis for production classes across " +
                    "all variants with type resolution"
            }

        val testTaskProvider =
            project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}Test") {
                it.group = "verification"
                it.description = "Run detekt analysis for test classes across " +
                    "all variants with type resolution"
            }

        val mainBaselineTaskProvider =
            project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}Main") {
                it.group = "verification"
                it.description = "Creates detekt baseline files for production classes across " +
                    "all variants with type resolution"
            }

        val testBaselineTaskProvider =
            project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}Test") {
                it.group = "verification"
                it.description = "Creates detekt baseline files for test classes across " +
                    "all variants with type resolution"
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
                    variant.nestedComponents.forEach { testVariant ->
                        testTaskProvider.configure {
                            it.dependsOn(DetektPlugin.DETEKT_TASK_NAME + testVariant.name.capitalize())
                        }
                        testBaselineTaskProvider.configure {
                            it.dependsOn(DetektPlugin.BASELINE_TASK_NAME + testVariant.name.capitalize())
                        }
                    }
                }
            }
        }
    }
}
