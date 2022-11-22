package io.gitlab.arturbosch.detekt.internal

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

internal class DetektJvm(private val project: Project) {
    fun registerTasks(extension: DetektExtension) {
        project.extensions.getByType(KotlinJvmProjectExtension::class.java).target.compilations.all { compilation ->
            val inputSource = compilation.kotlinSourceSets
                .map { it.kotlin.sourceDirectories }
                .fold(project.files() as FileCollection) { collection, next -> collection.plus(next) }
            project.registerJvmDetektTask(compilation, extension, inputSource)
            project.registerJvmCreateBaselineTask(compilation, extension, inputSource)
        }
    }

    private fun Project.registerJvmDetektTask(
        compilation: KotlinCompilation<KotlinCommonOptions>,
        extension: DetektExtension,
        inputSource: FileCollection
    ) {
        registerDetektTask(DetektPlugin.DETEKT_TASK_NAME + compilation.name.capitalize(), extension) {
            setSource(inputSource)
            classpath.setFrom(compilation.output.classesDirs, compilation.compileDependencyFiles)
            // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
            // We try to find the configured baseline or alternatively a specific variant matching this task.
            extension.baseline?.existingVariantOrBaseFile(compilation.name)?.let { baselineFile ->
                baseline.convention(layout.file(provider { baselineFile }))
            }
            setReportOutputConventions(reports, extension, compilation.name)
            description = "EXPERIMENTAL: Run detekt analysis for ${compilation.name} classes with type resolution"
        }
    }

    private fun Project.registerJvmCreateBaselineTask(
        compilation: KotlinCompilation<KotlinCommonOptions>,
        extension: DetektExtension,
        inputSource: FileCollection
    ) {
        registerCreateBaselineTask(DetektPlugin.BASELINE_TASK_NAME + compilation.name.capitalize(), extension) {
            setSource(inputSource)
            classpath.setFrom(compilation.output.classesDirs, compilation.compileDependencyFiles)
            val variantBaselineFile = extension.baseline?.addVariantName(compilation.name)
            baseline.convention(layout.file(provider { variantBaselineFile }))
            description = "EXPERIMENTAL: Creates detekt baseline for ${compilation.name} classes with type resolution"
        }
    }
}
