package dev.detekt.gradle.plugin.internal

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetsContainer

internal object DetektKmpJvmCompilations {
    fun registerTasks(project: Project, extension: DetektExtension) {
        val kotlinExtension = project.extensions.getByName("kotlin")
        if (kotlinExtension !is KotlinTargetsContainer) return

        kotlinExtension.targets.matching { it.platformType in setOf(jvm, androidJvm) }.all { target ->
            target.compilations.all { compilation ->
                project.registerJvmCompilationDetektTask(extension, compilation, target)
                project.registerJvmCompilationCreateBaselineTask(extension, compilation, target)
            }
        }
    }
}
