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
            baseline.convention(extension.baseline)
            setSource(existingInputDirectoriesProvider(project, extension))
            setIncludes(DetektPlugin.defaultIncludes)
            setExcludes(DetektPlugin.defaultExcludes)
        }

        tasks.matching { it.name == LifecycleBasePlugin.CHECK_TASK_NAME }.configureEach {
            it.dependsOn(detektTaskProvider)
        }
    }

    private fun Project.registerCreateBaselineTask(extension: DetektExtension) {
        registerCreateBaselineTask(DetektPlugin.BASELINE_TASK_NAME, extension) {
            baseline.convention(extension.baseline)
            setSource(existingInputDirectoriesProvider(project, extension))
        }
    }

    private fun existingInputDirectoriesProvider(
        project: Project,
        extension: DetektExtension
    ): Provider<FileCollection> = project.provider { extension.source.filter { it.exists() } }
}
