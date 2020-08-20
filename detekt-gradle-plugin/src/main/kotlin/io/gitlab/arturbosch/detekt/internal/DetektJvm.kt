package io.gitlab.arturbosch.detekt.internal

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.internal.HasConvention
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File

internal class DetektJvm(private val project: Project) {
    fun registerDetektJvmTasks(extension: DetektExtension) {
        project.afterEvaluate {
            project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.all { sourceSet ->
                project.registerJvmDetektTask(extension, sourceSet)
                project.registerJvmCreateBaselineTask(extension, sourceSet)
            }
        }
    }

    private fun Project.registerJvmDetektTask(extension: DetektExtension, sourceSet: SourceSet) {
        val kotlinSourceSet = (sourceSet as HasConvention).convention.plugins["kotlin"] as? KotlinSourceSet
            ?: throw GradleException("Kotlin source set not found. Please report on detekt's issue tracker")
        registerDetektTask(DetektPlugin.DETEKT_TASK_NAME + sourceSet.name.capitalize(), extension) {
            setSource(kotlinSourceSet.kotlin.files)
            classpath.setFrom(sourceSet.compileClasspath, sourceSet.output.classesDirs.filter { it.exists() })
            // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
            // We try to find the configured baseline or alternatively a specific variant matching this task.
            extension.baseline?.existingVariantOrBaseFile(sourceSet.name)?.let { baselineFile ->
                baseline.set(layout.file(project.provider { baselineFile }))
            }
            reports.xml.setDefaultIfUnset(File(extension.reportsDir, sourceSet.name + ".xml"))
            reports.html.setDefaultIfUnset(File(extension.reportsDir, sourceSet.name + ".html"))
            reports.txt.setDefaultIfUnset(File(extension.reportsDir, sourceSet.name + ".txt"))
            description = "EXPERIMENTAL: Run detekt analysis for ${sourceSet.name} classes with type resolution"
        }
    }

    private fun Project.registerJvmCreateBaselineTask(extension: DetektExtension, sourceSet: SourceSet) {
        val kotlinSourceSet = (sourceSet as HasConvention).convention.plugins["kotlin"] as? KotlinSourceSet
            ?: throw GradleException("Kotlin source set not found. Please report on detekt's issue tracker")
        registerCreateBaselineTask(DetektPlugin.BASELINE_TASK_NAME + sourceSet.name.capitalize(), extension) {
            setSource(kotlinSourceSet.kotlin.files)
            classpath.setFrom(sourceSet.compileClasspath, sourceSet.output.classesDirs.filter { it.exists() })
            val variantBaselineFile = extension.baseline?.addVariantName(sourceSet.name)
            baseline.set(project.layout.file(project.provider { variantBaselineFile }))
            description = "EXPERIMENTAL: Creates detekt baseline for ${sourceSet.name} classes with type resolution"
        }
    }
}
