package dev.detekt.gradle.plugin.internal

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.internal.addVariantName
import io.gitlab.arturbosch.detekt.internal.existingVariantOrBaseFile
import io.gitlab.arturbosch.detekt.internal.registerCreateBaselineTask
import io.gitlab.arturbosch.detekt.internal.registerDetektTask
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal fun Project.registerJvmCompilationDetektTask(
    extension: DetektExtension,
    compilation: KotlinCompilation<KotlinCommonOptions>,
    target: KotlinTarget? = null,
) {
    val taskSuffix = if (target != null) compilation.name + target.name.capitalize() else compilation.name
    registerDetektTask(DetektPlugin.DETEKT_TASK_NAME + taskSuffix.capitalize(), extension) {
        val siblingTask = compilation.compileTaskProvider.get() as KotlinJvmCompile

        setSource(siblingTask.sources)
        classpath.setFrom(compilation.output.classesDirs, siblingTask.libraries)

        // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
        // We try to find the configured baseline or alternatively a specific variant matching this task.
        extension.baseline.asFile.orNull?.existingVariantOrBaseFile(compilation.name)?.let { baselineFile ->
            baseline.convention(layout.file(provider { baselineFile }))
        }
        description = if (target != null) {
            "EXPERIMENTAL: Run detekt analysis for compilation ${compilation.name} on target " +
                "${compilation.target.name} with type resolution"
        } else {
            "EXPERIMENTAL: Run detekt analysis for ${compilation.name} classes with type resolution"
        }
    }
}

internal fun Project.registerJvmCompilationCreateBaselineTask(
    extension: DetektExtension,
    compilation: KotlinCompilation<KotlinCommonOptions>,
    target: KotlinTarget? = null,
) {
    val taskSuffix = if (target != null) compilation.name + target.name.capitalize() else compilation.name
    registerCreateBaselineTask(DetektPlugin.BASELINE_TASK_NAME + taskSuffix.capitalize(), extension) {
        val siblingTask = compilation.compileTaskProvider.get() as KotlinJvmCompile

        setSource(siblingTask.sources)
        classpath.setFrom(compilation.output.classesDirs, siblingTask.libraries)

        val variantBaselineFile = extension.baseline.asFile.orNull?.addVariantName(compilation.name)
        baseline.convention(layout.file(provider { variantBaselineFile }))
        description = if (target != null) {
            "EXPERIMENTAL: Creates detekt baseline for compilation ${compilation.name} on target " +
                "${compilation.target.name} with type resolution"
        } else {
            "EXPERIMENTAL: Creates detekt baseline for ${compilation.name} classes with type resolution"
        }
    }
}
