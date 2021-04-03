package io.gitlab.arturbosch.detekt.internal

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal class DetektPlain(private val project: Project) {

    fun registerTasks(extension: DetektExtension) {
        project.registerDetektTask(extension)
        project.registerCreateBaselineTask(extension)
    }

    private fun Project.registerDetektTask(extension: DetektExtension) {
        val detektTaskProvider = registerDetektTask(DetektPlugin.DETEKT_TASK_NAME, extension) {
            extension.baseline?.takeIf { it.exists() }?.let { baselineFile ->
                baseline.set(project.layout.file(project.provider { baselineFile }))
            }
            setSource(existingInputDirectoriesProvider(project, extension))
            reportsDir.set(project.provider { extension.customReportsDir })
            reports = extension.reports
        }

        tasks.matching { it.name == LifecycleBasePlugin.CHECK_TASK_NAME }.configureEach {
            it.dependsOn(detektTaskProvider)
        }
    }

    private fun Project.registerCreateBaselineTask(extension: DetektExtension) {
        registerCreateBaselineTask(DetektPlugin.BASELINE_TASK_NAME, extension) {
            baseline.set(project.layout.file(project.provider { extension.baseline }))
            setSource(existingInputDirectoriesProvider(project, extension))
        }
    }

    private fun existingInputDirectoriesProvider(
        project: Project,
        extension: DetektExtension
    ): Provider<FileCollection> = project.provider { extension.input.filter { it.exists() } }
}
