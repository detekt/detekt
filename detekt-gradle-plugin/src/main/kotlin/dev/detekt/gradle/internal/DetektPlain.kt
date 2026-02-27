package dev.detekt.gradle.internal

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektPlugin
import dev.detekt.gradle.plugin.internal.mapExplicitArgMode
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

internal class DetektPlain(private val project: Project) {

    fun registerTasks(extension: DetektExtension) {
        project.registerDetektTask(extension)
        project.registerCreateBaselineTask(extension)
    }

    private fun Project.registerDetektTask(extension: DetektExtension) {
        val detektTaskProvider = tasks.register(DetektPlugin.DETEKT_TASK_NAME, Detekt::class.java) { detektTask ->
            detektTask.baseline.convention(extension.baseline)
            detektTask.setSource(existingInputDirectoriesProvider(project, extension))
            detektTask.setIncludes(DetektPlugin.defaultIncludes)
            detektTask.setExcludes(DetektPlugin.defaultExcludes)
        }

        plugins.withType(KotlinBasePlugin::class.java) {
            detektTaskProvider.configure { detektTask ->
                detektTask.explicitApi.convention(mapExplicitArgMode())
            }
        }

        tasks.matching { it.name == LifecycleBasePlugin.CHECK_TASK_NAME }.configureEach {
            it.dependsOn(detektTaskProvider)
        }
    }

    private fun Project.registerCreateBaselineTask(extension: DetektExtension) {
        val baselineTaskProvider =
            tasks.register(DetektPlugin.BASELINE_TASK_NAME, DetektCreateBaselineTask::class.java) {
                it.baseline.convention(extension.baseline)
                it.setSource(existingInputDirectoriesProvider(project, extension))
            }

        plugins.withType(KotlinBasePlugin::class.java) {
            baselineTaskProvider.configure { baselineTask ->
                baselineTask.explicitApi.convention(mapExplicitArgMode())
            }
        }
    }

    private fun existingInputDirectoriesProvider(
        project: Project,
        extension: DetektExtension,
    ): Provider<FileCollection> = project.provider { extension.source.filter { it.exists() } }
}
