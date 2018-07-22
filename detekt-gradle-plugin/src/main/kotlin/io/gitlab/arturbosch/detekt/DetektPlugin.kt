package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.ReportingBasePlugin
import java.io.File


/**
 * @author Marvin Ramin
 */
class DetektPlugin : Plugin<Project> {


	override fun apply(project: Project) {
		project.pluginManager.apply(ReportingBasePlugin::class.java)

		val extension = project.extensions.create(DETEKT, DetektExtension::class.java, project)

		configurePluginDependencies(project, extension)

		val detektTask = createAndConfigureDetektTask(project, extension)
		createAndConfigureCreateBaselineTask(project, extension)

		project.tasks.create(GENERATE_CONFIG, DetektGenerateConfigTask::class.java) { detekt = detektTask }
		project.tasks.create(IDEA_FORMAT, DetektIdeaFormatTask::class.java) { detekt = detektTask }
		project.tasks.create(IDEA_INSPECT, DetektIdeaInspectionTask::class.java) { detekt = detektTask }
	}

	private fun createAndConfigureDetektTask(project: Project, extension: DetektExtension): Detekt {
		val detektTask = project.tasks.create(DETEKT, Detekt::class.java) {
			val detekt = this
			detekt.debug = extension.debugProperty
			detekt.parallel = extension.parallelProperty
			detekt.disableDefaultRuleSets = extension.disableDefaultRuleSetsProperty
			detekt.filters = extension.filtersProperty
			detekt.config = extension.configProperty
			detekt.baseline = extension.baselineProperty
			detekt.input.set(project.provider {
				extension.input ?: extension.defaultSourceDirectories.filter { it.exists() }
			})

			project.tasks.getByNameLater(Task::class.java, JavaBasePlugin.CHECK_TASK_NAME).configure {
				dependsOn(detekt)
			}
		}

		project.afterEvaluate {
			detektTask.reports.all {
				extension.reports.withName(name)?.let {
					val reportExtension = it
					isEnabled = reportExtension.enabled
					val fileSuffix = name
					val reportsDir = extension.reportsDir ?: extension.defaultReportsDir
					val customDestination = reportExtension.destination
					destination = customDestination ?: File(reportsDir, "${DETEKT}.$fileSuffix")
				}
			}
		}

		return detektTask
	}

	private fun createAndConfigureCreateBaselineTask(project: Project, extension: DetektExtension) =
			project.tasks.createLater(BASELINE, DetektCreateBaselineTask::class.java) {
				val task = this
				task.baseline = extension.baselineProperty
				task.debug = extension.debugProperty
				task.parallel = extension.parallelProperty
				task.disableDefaultRuleSets = extension.disableDefaultRuleSetsProperty
				task.filters = extension.filtersProperty
				task.config = extension.configProperty
				task.input.set(project.provider {
					extension.input ?: extension.defaultSourceDirectories.filter { it.exists() }
				})
			}

	private fun configurePluginDependencies(project: Project, extension: DetektExtension) =
			project.configurations.create(DETEKT) {
				isVisible = false
				isTransitive = true
				description = "The $DETEKT libraries to be used for this project."
				defaultDependencies {
					val version = extension.toolVersion ?: DEFAULT_DETEKT_VERSION
					add(project.dependencies.create("io.gitlab.arturbosch.detekt:detekt-cli:$version"))
				}
			}


	companion object {
		private const val DEFAULT_DETEKT_VERSION = "1.0.0-GRADLE"
		private const val DETEKT = "detekt"
		private const val IDEA_FORMAT = "detektIdeaFormat"
		private const val IDEA_INSPECT = "detektIdeaInspect"
		private const val GENERATE_CONFIG = "detektGenerateConfig"
		private const val BASELINE = "detektBaseline"
	}
}
