package dev.detekt.gradle.plugin.internal

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.internal.addVariantName
import dev.detekt.gradle.internal.existingVariantOrBaseFile
import dev.detekt.gradle.plugin.DetektPlugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal fun Project.registerJvmCompilationDetektTask(
    extension: DetektExtension,
    compilation: KotlinCompilation<*>,
    target: KotlinTarget? = null,
) {
    val taskSuffix = if (target != null) compilation.name + target.name.capitalize() else compilation.name
    tasks.register(DetektPlugin.DETEKT_TASK_NAME + taskSuffix.capitalize(), Detekt::class.java) { detektTask ->
        val siblingTask = compilation.compileTaskProvider.map { it as KotlinJvmCompile }

        detektTask.setSource(siblingTask.map { it.sources })
        detektTask.classpath.conventionCompat(
            compilation.output.classesDirs,
            siblingTask.map { it.libraries }
        )
        detektTask.friendPaths.conventionCompat(
            compilation.output.classesDirs,
            siblingTask.map { it.friendPaths }
        )
        detektTask.apiVersion.convention(
            siblingTask.flatMap { task -> task.compilerOptions.apiVersion.map { it.version } }
        )
        detektTask.languageVersion.convention(
            siblingTask.flatMap { task -> task.compilerOptions.languageVersion.map { it.version } }
        )
        /* Note: jvmTarget convention is also set in setDetektTaskDefaults. There may be a race between setting it here
           as well, but they should both set the same value. This should possibly be revisited in the future. */
        detektTask.jvmTarget.convention(
            siblingTask.flatMap { task -> task.compilerOptions.jvmTarget.map { it.target } }
        )
        detektTask.freeCompilerArgs.convention(siblingTask.flatMap { it.compilerOptions.freeCompilerArgs })
        detektTask.optIn.convention(siblingTask.flatMap { it.compilerOptions.optIn })
        detektTask.noJdk.convention(siblingTask.flatMap { it.compilerOptions.noJdk })
        detektTask.multiPlatformEnabled.convention(siblingTask.flatMap { it.multiPlatformEnabled })
        if (compilation.name == "main") {
            detektTask.explicitApi.convention(mapExplicitArgMode())
        }

        detektTask.baseline.convention(
            project.layout.file(
                extension.baseline.flatMap {
                    providers.provider { it.asFile.existingVariantOrBaseFile(compilation.name) }
                }
            )
        )
        detektTask.description = if (target != null) {
            "Run detekt analysis for compilation ${compilation.name} on target " +
                "${compilation.target.name} with type resolution"
        } else {
            "Run detekt analysis for ${compilation.name} classes with type resolution"
        }
    }
}

internal fun Project.registerJvmCompilationCreateBaselineTask(
    extension: DetektExtension,
    compilation: KotlinCompilation<*>,
    target: KotlinTarget? = null,
) {
    val taskSuffix = if (target != null) compilation.name + target.name.capitalize() else compilation.name
    tasks.register(
        DetektPlugin.BASELINE_TASK_NAME + taskSuffix.capitalize(),
        DetektCreateBaselineTask::class.java,
    ) { createBaselineTask ->
        val siblingTask = compilation.compileTaskProvider.map { it as KotlinJvmCompile }

        createBaselineTask.setSource(siblingTask.map { it.sources })
        createBaselineTask.classpath.conventionCompat(
            compilation.output.classesDirs,
            siblingTask.map { it.libraries }
        )
        createBaselineTask.friendPaths.conventionCompat(
            compilation.output.classesDirs,
            siblingTask.map { it.friendPaths }
        )
        createBaselineTask.apiVersion.convention(
            siblingTask.flatMap { task -> task.compilerOptions.apiVersion.map { it.version } }
        )
        createBaselineTask.languageVersion.convention(
            siblingTask.flatMap { task -> task.compilerOptions.languageVersion.map { it.version } }
        )
        /* Note: jvmTarget convention is also set in setCreateBaselineTaskDefaults. There may be a race between setting
           it here as well, but they should both set the same value. This should possibly be revisited in the future. */
        createBaselineTask.jvmTarget.convention(
            siblingTask.flatMap { task -> task.compilerOptions.jvmTarget.map { it.target } }
        )
        createBaselineTask.freeCompilerArgs.convention(siblingTask.flatMap { it.compilerOptions.freeCompilerArgs })
        createBaselineTask.optIn.convention(siblingTask.flatMap { it.compilerOptions.optIn })
        createBaselineTask.noJdk.convention(siblingTask.flatMap { it.compilerOptions.noJdk })
        createBaselineTask.multiPlatformEnabled.convention(siblingTask.flatMap { it.multiPlatformEnabled })
        if (compilation.name == "main") {
            createBaselineTask.explicitApi.convention(mapExplicitArgMode())
        }

        createBaselineTask.baseline.convention(
            project.layout.file(
                extension.baseline.flatMap {
                    providers.provider { it.asFile.addVariantName(compilation.name) }
                }
            )
        )
        createBaselineTask.description = if (target != null) {
            "Creates detekt baseline for compilation ${compilation.name} on target " +
                "${compilation.target.name} with type resolution"
        } else {
            "Creates detekt baseline for ${compilation.name} classes with type resolution"
        }
    }
}

internal fun Project.mapExplicitArgMode(): Provider<String> =
    provider {
        val kotlinExtension = extensions.getByType(KotlinBaseExtension::class.java)
        when (kotlinExtension.explicitApi) {
            ExplicitApiMode.Strict -> "strict"
            ExplicitApiMode.Warning -> "warning"
            else -> null
        }
    }
