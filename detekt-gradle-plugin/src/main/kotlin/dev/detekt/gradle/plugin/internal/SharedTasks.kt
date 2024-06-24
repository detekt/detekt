package dev.detekt.gradle.plugin.internal

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.internal.addVariantName
import io.gitlab.arturbosch.detekt.internal.existingVariantOrBaseFile
import io.gitlab.arturbosch.detekt.internal.registerCreateBaselineTask
import io.gitlab.arturbosch.detekt.internal.registerDetektTask
import org.gradle.api.Project
import org.gradle.util.GradleVersion
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
        if (GradleVersion.current() >= GradleVersion.version("8.8")) {
            classpath.convention(compilation.output.classesDirs, siblingTask.libraries)
        } else {
            classpath.setFrom(compilation.output.classesDirs, siblingTask.libraries)
        }
        apiVersion.convention(siblingTask.compilerOptions.apiVersion.map { it.version })
        languageVersion.convention(siblingTask.compilerOptions.languageVersion.map { it.version })
        /* Note: jvmTarget convention is also set in setDetektTaskDefaults. There may be a race between setting it here
           as well, but they should both set the same value. This should possibly be revisited in the future. */
        jvmTarget.convention(siblingTask.compilerOptions.jvmTarget.map { it.target })

        baseline.convention(
            project.layout.file(
                extension.baseline.flatMap {
                    providers.provider { it.asFile.existingVariantOrBaseFile(compilation.name) }
                }
            )
        )
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
        if (GradleVersion.current() >= GradleVersion.version("8.8")) {
            classpath.convention(compilation.output.classesDirs, siblingTask.libraries)
        } else {
            classpath.setFrom(compilation.output.classesDirs, siblingTask.libraries)
        }
        apiVersion.convention(siblingTask.compilerOptions.apiVersion.map { it.version })
        languageVersion.convention(siblingTask.compilerOptions.languageVersion.map { it.version })
        /* Note: jvmTarget convention is also set in setCreateBaselineTaskDefaults. There may be a race between setting
           it here as well, but they should both set the same value. This should possibly be revisited in the future. */
        jvmTarget.convention(siblingTask.compilerOptions.jvmTarget.map { it.target })

        baseline.convention(
            project.layout.file(
                extension.baseline.flatMap {
                    providers.provider { it.asFile.addVariantName(compilation.name) }
                }
            )
        )
        description = if (target != null) {
            "EXPERIMENTAL: Creates detekt baseline for compilation ${compilation.name} on target " +
                "${compilation.target.name} with type resolution"
        } else {
            "EXPERIMENTAL: Creates detekt baseline for ${compilation.name} classes with type resolution"
        }
    }
}
