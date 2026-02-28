package dev.detekt.gradle.internal

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask
import dev.detekt.gradle.DetektProfilingTask
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektPlugin
import dev.detekt.gradle.plugin.DetektPlugin.Companion.PROFILING_ROOT_TASK_NAME
import dev.detekt.gradle.plugin.DetektPlugin.Companion.PROFILING_TASK_NAME
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal class DetektPlain(private val project: Project) {

    fun registerTasks(extension: DetektExtension) {
        val detektProvider = project.registerDetektTask(extension)
        project.registerCreateBaselineTask(extension)
        project.registerDetektProfilingTask(extension, detektProvider)
    }

    private fun Project.registerDetektTask(extension: DetektExtension): TaskProvider<Detekt> {
        val detektTaskProvider = tasks.register(DetektPlugin.DETEKT_TASK_NAME, Detekt::class.java) { detektTask ->
            detektTask.baseline.convention(extension.baseline)
            detektTask.setSource(existingInputDirectoriesProvider(project, extension))
            detektTask.setIncludes(DetektPlugin.defaultIncludes)
            detektTask.setExcludes(DetektPlugin.defaultExcludes)
        }

        tasks.matching { it.name == LifecycleBasePlugin.CHECK_TASK_NAME }.configureEach {
            it.dependsOn(detektTaskProvider)
        }

        return detektTaskProvider
    }

    private fun Project.registerCreateBaselineTask(extension: DetektExtension) {
        tasks.register(DetektPlugin.BASELINE_TASK_NAME, DetektCreateBaselineTask::class.java) {
            it.baseline.convention(extension.baseline)
            it.setSource(existingInputDirectoriesProvider(project, extension))
        }
    }

    private fun Project.registerDetektProfilingTask(
        extension: DetektExtension,
        detektTaskProvider: TaskProvider<Detekt>,
    ) {
        if (project == rootProject) {
            // For the root project, register profiling task with a different name
            // so the aggregate will get executed when using the regular task name
            DetektProfilingTask.register(
                project = project,
                taskName = PROFILING_ROOT_TASK_NAME,
                consumableConfigurationName = "${PROFILING_TASK_NAME}Publisher",
                extension = extension,
                detektTaskProvider = detektTaskProvider
            )
        } else {
            DetektProfilingTask.register(
                project = project,
                taskName = PROFILING_TASK_NAME,
                extension = extension,
                detektTaskProvider = detektTaskProvider
            )
        }
    }

    private fun existingInputDirectoriesProvider(
        project: Project,
        extension: DetektExtension,
    ): Provider<FileCollection> = project.provider { extension.source.filter { it.exists() } }
}
