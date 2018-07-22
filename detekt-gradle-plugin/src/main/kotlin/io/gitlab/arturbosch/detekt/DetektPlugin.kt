package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
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

		createAndConfigureDetektTask(project, extension)
		createAndConfigureCreateBaselineTask(project, extension)
		createAndConfigureGenerateConfigTask(project, extension)

		createAndConfigureIdeaTasks(project, extension)
	}

	private fun createAndConfigureDetektTask(project: Project, extension: DetektExtension) {
		val detektTask = project.tasks.createLater(DETEKT, Detekt::class.java) {
			debugOrDefault = extension.debug
			parallelOrDefault = extension.parallel
			disableDefaultRuleSetsOrDefault = extension.disableDefaultRuleSets
			filters = extension.filtersProperty
			config = extension.configProperty
			baseline = extension.baselineProperty
			input.set(inputProvider(project, extension))
			reports.all {
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

		project.tasks.findByName(JavaBasePlugin.CHECK_TASK_NAME)?.dependsOn(detektTask)
	}

	private fun createAndConfigureCreateBaselineTask(project: Project, extension: DetektExtension) =
			project.tasks.createLater(BASELINE, DetektCreateBaselineTask::class.java) {
				baseline = extension.baselineProperty
				debugOrDefault = extension.debug
				parallelOrDefault = extension.parallel
				disableDefaultRuleSetsOrDefault = extension.disableDefaultRuleSets
				filters = extension.filtersProperty
				config = extension.configProperty
				input.set(inputProvider(project, extension))
			}

	private fun createAndConfigureGenerateConfigTask(project: Project, extension: DetektExtension) =
			project.tasks.createLater(GENERATE_CONFIG, DetektGenerateConfigTask::class.java) {
				input.set(inputProvider(project, extension))
			}

	private fun createAndConfigureIdeaTasks(project: Project, extension: DetektExtension) {
		project.tasks.createLater(IDEA_FORMAT, DetektIdeaFormatTask::class.java) {
			debugOrDefault = extension.debug
			input.set(inputProvider(project, extension))
			ideaExtension = extension.idea
		}

		project.tasks.createLater(IDEA_INSPECT, DetektIdeaInspectionTask::class.java) {
			debugOrDefault = extension.debug
			input.set(inputProvider(project, extension))
			ideaExtension = extension.idea
		}
	}

	private fun inputProvider(project: Project, extension: DetektExtension) =
			project.provider {
				extension.input ?: extension.defaultSourceDirectories.filter { it.exists() }
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
