package io.gitlab.arturbosch.detekt.internal

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.common
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm
import java.io.File

internal class DetektMultiplatform(private val project: Project) {

    fun registerTasks(extension: DetektExtension) {
        project.afterEvaluate {
            project.registerMultiplatformTasks(extension)
        }
    }

    private fun Project.registerMultiplatformTasks(extension: DetektExtension) {
        // We need another project.afterEvaluate as the Android target is attached on
        // a project.afterEvaluate inside AGP. We should further investigate and potentially remove this.
        project.afterEvaluate {
            val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

            kmpExtension.targets.all { target ->
                target.compilations.forEach { compilation ->
                    val taskSuffix = target.name.capitalize() + compilation.name.capitalize()

                    // We currently run type resolution only for Jvm & Android targets as
                    // native/js targets needs a different compiler classpath.
                    val runWithTypeResolution = when (target.platformType) {
                        jvm -> true
                        androidJvm -> true
                        else -> false
                    }

                    val inputSource = compilation.kotlinSourceSets.map {
                        it.kotlin.sourceDirectories
                    }.fold(project.files() as FileCollection) { collection, next ->
                        collection.plus(next)
                    }

                    val detektTaskProvider = project.registerDetektTask(
                        DetektPlugin.DETEKT_TASK_NAME + taskSuffix,
                        extension
                    ) {
                        setSource(inputSource)
                        if (runWithTypeResolution) {
                            classpath.setFrom(inputSource, compilation.compileDependencyFiles)
                        }
                        // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
                        // We try to find the configured baseline or alternatively a specific variant matching this task.
                        extension.baseline?.existingVariantOrBaseFile(taskSuffix)?.let { baselineFile ->
                            baseline.set(layout.file(project.provider { baselineFile }))
                        }
                        reports = extension.reports
                        reports.xml.setDefaultIfUnset(File(extension.reportsDir, compilation.name + ".xml"))
                        reports.html.setDefaultIfUnset(File(extension.reportsDir, compilation.name + ".html"))
                        reports.txt.setDefaultIfUnset(File(extension.reportsDir, compilation.name + ".txt"))
                        reports.sarif.setDefaultIfUnset(File(extension.reportsDir, compilation.name + ".sarif"))
                        description = "Run detekt analysis for target ${target.name} and source set ${compilation.name}"
                        if (runWithTypeResolution) {
                            description = "EXPERIMENTAL: $description with type resolution."
                        }
                    }

                    tasks.matching { it.name == LifecycleBasePlugin.CHECK_TASK_NAME }.configureEach {
                        it.dependsOn(detektTaskProvider)
                    }

                    project.registerCreateBaselineTask(DetektPlugin.BASELINE_TASK_NAME + taskSuffix, extension) {
                        setSource(inputSource)
                        if (runWithTypeResolution) {
                            classpath.setFrom(inputSource, compilation.compileDependencyFiles)
                        }
                        val variantBaselineFile = extension.baseline?.addVariantName(taskSuffix)
                        baseline.set(project.layout.file(project.provider { variantBaselineFile }))

                        description = "Creates detekt baseline for ${target.name} and source set ${compilation.name}"
                        if (runWithTypeResolution) {
                            description = "EXPERIMENTAL: $description with type resolution."
                        }
                    }
                }
            }
        }
    }
}
