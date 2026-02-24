package dev.detekt.gradle.plugin.internal

import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

internal object DetektJvmCompilations {
    fun registerTasks(project: Project, extension: DetektExtension) {
        project.extensions.getByType(KotlinJvmExtension::class.java).target.compilations.configureEach { compilation ->
            project.registerJvmCompilationDetektTask(extension, compilation)
            project.registerJvmCompilationCreateBaselineTask(extension, compilation)
        }
    }
}
