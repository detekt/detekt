package io.gitlab.arturbosch.detekt.internal

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.HasConvention
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

internal class DetektJvm(private val project: Project) {
    fun registerTasks(extension: DetektExtension) {
        project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.all { sourceSet ->
            project.registerJvmDetektTask(extension, sourceSet)
            project.registerJvmCreateBaselineTask(extension, sourceSet)
        }
    }

    private fun Project.registerJvmDetektTask(extension: DetektExtension, sourceSet: SourceSet) {
        val kotlinSourceSet = sourceSet.kotlin ?: project.objects.sourceDirectorySet("empty", "Empty kotlin source set")
        registerDetektTask(DetektPlugin.DETEKT_TASK_NAME + sourceSet.name.capitalize(), extension) {
            source = kotlinSourceSet
            classpath.setFrom(sourceSet.compileClasspath.existingFiles(), sourceSet.output.classesDirs.existingFiles())
            // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
            // We try to find the configured baseline or alternatively a specific variant matching this task.
            extension.baseline?.existingVariantOrBaseFile(sourceSet.name)?.let { baselineFile ->
                baseline.set(layout.file(project.provider { baselineFile }))
            }
            setReportOutputConventions(reports, extension, sourceSet.name)
            description = "EXPERIMENTAL: Run detekt analysis for ${sourceSet.name} classes with type resolution"
        }
    }

    private fun Project.registerJvmCreateBaselineTask(extension: DetektExtension, sourceSet: SourceSet) {
        val kotlinSourceSet = sourceSet.kotlin ?: project.objects.sourceDirectorySet("empty", "Empty kotlin source set")
        registerCreateBaselineTask(DetektPlugin.BASELINE_TASK_NAME + sourceSet.name.capitalize(), extension) {
            source = kotlinSourceSet
            classpath.setFrom(sourceSet.compileClasspath.existingFiles(), sourceSet.output.classesDirs.existingFiles())
            val variantBaselineFile = extension.baseline?.addVariantName(sourceSet.name)
            baseline.set(project.layout.file(project.provider { variantBaselineFile }))
            description = "EXPERIMENTAL: Creates detekt baseline for ${sourceSet.name} classes with type resolution"
        }
    }

    private fun FileCollection.existingFiles() = filter { it.exists() }

    private val SourceSet.kotlin: SourceDirectorySet?
        get() = ((this as HasConvention).convention.plugins["kotlin"] as? KotlinSourceSet)?.kotlin
}
