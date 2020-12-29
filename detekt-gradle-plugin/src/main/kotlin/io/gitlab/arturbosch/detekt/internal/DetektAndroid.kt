package io.gitlab.arturbosch.detekt.internal

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.CommonExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.io.File

internal class DetektAndroid(private val project: Project) {

    private val allTasksProvider: TaskProvider<Task> by lazy {
        project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}All") {
            it.group = "verification"
            it.description = "EXPERIMENTAL: Creates detekt baseline files for all classes across " +
                "all variants with type resolution"
        }
    }

    private val baselineTasksProvider: TaskProvider<Task> by lazy {
        project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}All") {
            it.group = "verification"
            it.description = "EXPERIMENTAL: Creates detekt baseline files for all classes across " +
                "all variants with type resolution"
        }
    }

    fun registerDetektAndroidTasks(extension: DetektExtension) {
        // Ideally we don't need this, but if the user configures Android CommonExtension before Detekt Extension,
        // sourceSetFilter will not work.
        project.afterEvaluate {
            // All Android plugins consume their extension extending on CommonExtension.
            project.extensions.configure(CommonExtension::class.java) { androidExtension ->
                androidExtension.sourceSets.all { sourceSet ->
                    if (extension.sourceSetFilter.test(sourceSet.name)) {
                        val detektTask = project.registerAndroidDetektTask(sourceSet, extension)
                        allTasksProvider.configure { it.dependsOn(detektTask) }
                        val baselineTask = project.registerAndroidCreateBaselineTask(sourceSet, extension)
                        baselineTasksProvider.configure { it.dependsOn(baselineTask) }
                    }
                }
            }
        }
    }

    private fun Project.registerAndroidDetektTask(
        sourceSet: AndroidSourceSet,
        extension: DetektExtension,
    ): TaskProvider<Detekt> =
        registerDetektTask(DetektPlugin.DETEKT_TASK_NAME + sourceSet.name.capitalize(), extension) {
            source = project.files().asFileTree.matching(sourceSet.java)
            classpath.setFrom(
                configurations.getByName(sourceSet.apiConfigurationName),
                configurations.getByName(sourceSet.implementationConfigurationName),
                configurations.getByName(sourceSet.compileOnlyConfigurationName)
            )
            // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
            // We try to find the configured baseline or alternatively a specific variant matching this task.
            extension.baseline?.existingVariantOrBaseFile(sourceSet.name)?.let { baselineFile ->
                baseline.set(layout.file(project.provider { baselineFile }))
            }
            reports = extension.reports
            reports.xml.setDefaultIfUnset(File(extension.reportsDir, sourceSet.name + ".xml"))
            reports.html.setDefaultIfUnset(File(extension.reportsDir, sourceSet.name + ".html"))
            reports.txt.setDefaultIfUnset(File(extension.reportsDir, sourceSet.name + ".txt"))
            reports.sarif.setDefaultIfUnset(File(extension.reportsDir, sourceSet.name + ".sarif"))
            description = "EXPERIMENTAL: Run detekt analysis for ${sourceSet.name} classes with type resolution"
        }

    private fun Project.registerAndroidCreateBaselineTask(
        sourceSet: AndroidSourceSet,
        extension: DetektExtension
    ): TaskProvider<DetektCreateBaselineTask> =
        registerCreateBaselineTask(DetektPlugin.BASELINE_TASK_NAME + sourceSet.name.capitalize(), extension) {
            source = project.files().asFileTree.matching(sourceSet.java)
            classpath.setFrom(
                configurations.getByName(sourceSet.apiConfigurationName),
                configurations.getByName(sourceSet.implementationConfigurationName),
                configurations.getByName(sourceSet.compileOnlyConfigurationName)
            )
            val variantBaselineFile = extension.baseline?.addVariantName(sourceSet.name)
            baseline.set(project.layout.file(project.provider { variantBaselineFile }))
            description = "EXPERIMENTAL: Creates detekt baseline for ${sourceSet.name} classes with type resolution"
        }
}
