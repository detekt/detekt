package dev.detekt.gradle.plugin.internal

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal object DetektJvmCompilations {
    fun registerTasks(project: Project, extension: DetektExtension) {
        project.extensions.getByType(KotlinJvmProjectExtension::class.java).target.compilations.all { compilation ->
            project.registerJvmCompilationDetektTask(extension, compilation)
            project.registerJvmCompilationCreateBaselineTask(extension, compilation)
        }
    }
}
