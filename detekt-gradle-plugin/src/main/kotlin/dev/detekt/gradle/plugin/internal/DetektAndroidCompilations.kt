@file:Suppress("DEPRECATION")

package dev.detekt.gradle.plugin.internal

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.TestedVariant
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal object DetektAndroidCompilations {
    fun registerTasks(project: Project, extension: DetektExtension) {
        project.extensions.getByType(KotlinAndroidProjectExtension::class.java).target.compilations.all { compilation ->
            project.registerJvmCompilationDetektTask(extension, compilation)
            project.registerJvmCompilationCreateBaselineTask(extension, compilation)
        }
    }

    private fun DetektExtension.matchesIgnoredConfiguration(variant: BaseVariant): Boolean =
        ignoredVariants.get().contains(variant.name) ||
            ignoredBuildTypes.get().contains(variant.buildType.name) ||
            ignoredFlavors.get().contains(variant.flavorName)

    fun linkTasks(project: Project, extension: DetektExtension) {
        val mainTaskProvider =
            project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}Main") {
                it.group = "verification"
                it.description = "EXPERIMENTAL: Run detekt analysis for production classes across " +
                    "all variants with type resolution"
            }

        val testTaskProvider =
            project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}Test") {
                it.group = "verification"
                it.description = "EXPERIMENTAL: Run detekt analysis for test classes across " +
                    "all variants with type resolution"
            }

        val mainBaselineTaskProvider =
            project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}Main") {
                it.group = "verification"
                it.description = "EXPERIMENTAL: Creates detekt baseline files for production classes across " +
                    "all variants with type resolution"
            }

        val testBaselineTaskProvider =
            project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}Test") {
                it.group = "verification"
                it.description = "EXPERIMENTAL: Creates detekt baseline files for test classes across " +
                    "all variants with type resolution"
            }

        fun variants(extension: BaseExtension): DomainObjectSet<out BaseVariant>? = when (extension) {
            is AppExtension -> extension.applicationVariants
            is LibraryExtension -> extension.libraryVariants
            is TestExtension -> extension.applicationVariants
            else -> null
        }

        fun testVariants(baseVariant: BaseVariant): List<BaseVariant> = if (baseVariant is TestedVariant) {
            listOfNotNull(baseVariant.testVariant, baseVariant.unitTestVariant)
        } else {
            emptyList()
        }

        // There is not a single Android plugin, but each registers an extension based on BaseExtension,
        // so we catch them all by looking for this one
        project.extensions.findByType(BaseExtension::class.java)?.let { baseExtension ->
            variants(baseExtension)
                ?.matching { !extension.matchesIgnoredConfiguration(it) }
                ?.all { variant ->
                    mainTaskProvider.configure {
                        it.dependsOn(DetektPlugin.DETEKT_TASK_NAME + variant.name.capitalize())
                    }
                    mainBaselineTaskProvider.configure {
                        it.dependsOn(DetektPlugin.BASELINE_TASK_NAME + variant.name.capitalize())
                    }
                    testVariants(variant)
                        .filter { !extension.matchesIgnoredConfiguration(it) }
                        .forEach { testVariant ->
                            testTaskProvider.configure {
                                it.dependsOn(DetektPlugin.DETEKT_TASK_NAME + testVariant.name.capitalize())
                            }
                            testBaselineTaskProvider.configure {
                                DetektPlugin.BASELINE_TASK_NAME + testVariant.name.capitalize()
                            }
                        }
                }
        }
    }
}
