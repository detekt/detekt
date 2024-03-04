package dev.detekt.gradle.plugin.internal

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.internal.addVariantName
import io.gitlab.arturbosch.detekt.internal.existingVariantOrBaseFile
import io.gitlab.arturbosch.detekt.internal.registerCreateBaselineTask
import io.gitlab.arturbosch.detekt.internal.registerDetektTask
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal object DetektJvmCompilations {
    fun registerTasks(project: Project, extension: DetektExtension) {
        project.extensions.getByType(KotlinJvmProjectExtension::class.java).target.compilations.all { compilation ->
            project.registerJvmDetektTask(compilation, extension)
            project.registerJvmCreateBaselineTask(compilation, extension)
        }
    }

    private fun Project.registerJvmDetektTask(
        compilation: KotlinCompilation<KotlinCommonOptions>,
        extension: DetektExtension,
    ) {
        registerDetektTask(DetektPlugin.DETEKT_TASK_NAME + compilation.name.capitalize(), extension) {
            val siblingTask = compilation.compileTaskProvider.get() as KotlinJvmCompile

            setSource(siblingTask.sources)
            classpath.setFrom(compilation.output.classesDirs, siblingTask.libraries)

            // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
            // We try to find the configured baseline or alternatively a specific variant matching this task.
            extension.baseline.asFile.orNull?.existingVariantOrBaseFile(compilation.name)?.let { baselineFile ->
                baseline.convention(layout.file(provider { baselineFile }))
            }
            description = "EXPERIMENTAL: Run detekt analysis for ${compilation.name} classes with type resolution"
        }
    }

    private fun Project.registerJvmCreateBaselineTask(
        compilation: KotlinCompilation<KotlinCommonOptions>,
        extension: DetektExtension,
    ) {
        registerCreateBaselineTask(DetektPlugin.BASELINE_TASK_NAME + compilation.name.capitalize(), extension) {
            val siblingTask = compilation.compileTaskProvider.get() as KotlinJvmCompile

            setSource(siblingTask.sources)
            classpath.setFrom(compilation.output.classesDirs, siblingTask.libraries)

            val variantBaselineFile = extension.baseline.asFile.orNull?.addVariantName(compilation.name)
            baseline.convention(layout.file(provider { variantBaselineFile }))
            description = "EXPERIMENTAL: Creates detekt baseline for ${compilation.name} classes with type resolution"
        }
    }
}
