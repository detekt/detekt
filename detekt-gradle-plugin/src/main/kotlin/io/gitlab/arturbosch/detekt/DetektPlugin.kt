package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * @author Marvin Ramin
 * @author Markus Schwarz
 */
class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val extension = project.extensions.create(DETEKT, DetektExtension::class.java, project)

		configurePluginDependencies(project, extension)

		createAndConfigureDetektTask(project, extension)
		createAndConfigureCreateBaselineTask(project, extension)
		createAndConfigureGenerateConfigTask(project, extension)

		createAndConfigureIdeaTasks(project, extension)
	}

	private fun createAndConfigureDetektTask(project: Project, extension: DetektExtension) {
		val detektTask = project.tasks.register(DETEKT, Detekt::class.java) {
			it.debugProp.set(project.provider { extension.debug })
			it.parallelProp.set(project.provider { extension.parallel })
			it.disableDefaultRuleSetsProp.set(project.provider { extension.disableDefaultRuleSets })
			it.filters.set(project.provider { extension.filters })
			it.config.setFrom(project.provider { extension.config })
			it.baseline.set(project.layout.file(project.provider { extension.baselineOrDefaultIfExists }))
			it.plugins.set(project.provider { extension.plugins })
			it.input.setFrom(existingInputDirectoriesProvider(project, extension))
			it.reportsDir.set(project.provider { extension.customReportsDir })
			it.reports = extension.reports
		}

		project.tasks.findByName(LifecycleBasePlugin.CHECK_TASK_NAME)?.dependsOn(detektTask)
	}

	private fun createAndConfigureCreateBaselineTask(project: Project, extension: DetektExtension) =
			project.tasks.register(BASELINE, DetektCreateBaselineTask::class.java) {
				it.baseline.set(project.layout.file(project.provider { extension.baseline }))
				it.config.setFrom(project.provider { extension.config })
				it.debug.set(project.provider { extension.debug })
				it.parallel.set(project.provider { extension.parallel })
				it.disableDefaultRuleSets.set(project.provider { extension.disableDefaultRuleSets })
				it.filters.set(project.provider { extension.filters })
				it.plugins.set(project.provider { extension.plugins })
				it.input.setFrom(existingInputDirectoriesProvider(project, extension))
			}

	private fun createAndConfigureGenerateConfigTask(project: Project, extension: DetektExtension) =
			project.tasks.register(GENERATE_CONFIG, DetektGenerateConfigTask::class.java) {
				it.input.setFrom(existingInputDirectoriesProvider(project, extension))
			}

	private fun createAndConfigureIdeaTasks(project: Project, extension: DetektExtension) {
		project.tasks.register(IDEA_FORMAT, DetektIdeaFormatTask::class.java) {
			it.debug.set(project.provider { extension.debug })
			it.input.setFrom(existingInputDirectoriesProvider(project, extension))
			it.ideaExtension = extension.idea
		}

		project.tasks.register(IDEA_INSPECT, DetektIdeaInspectionTask::class.java) {
			it.debug.set(project.provider { extension.debug })
			it.input.setFrom(existingInputDirectoriesProvider(project, extension))
			it.ideaExtension = extension.idea
		}
	}

	private fun existingInputDirectoriesProvider(project: Project, extension: DetektExtension): Provider<FileCollection> =
			project.provider { extension.input.filter { it.exists() } }

	private fun configurePluginDependencies(project: Project, extension: DetektExtension) {
		project.configurations.create(CONFIGURATION_DETEKT_PLUGINS) { configuration ->
			configuration.isVisible = false
			configuration.isTransitive = true
			configuration.description = "The $CONFIGURATION_DETEKT_PLUGINS libraries to be used for this project."
		}

		project.configurations.create(CONFIGURATION_DETEKT) { configuration ->
			configuration.isVisible = false
			configuration.isTransitive = true
			configuration.description = "The $CONFIGURATION_DETEKT dependencies to be used for this project."

			configuration.defaultDependencies { dependencySet ->
				@Suppress("USELESS_ELVIS")
				val version = extension.toolVersion ?: DEFAULT_DETEKT_VERSION
				dependencySet.add(project.dependencies.create("io.gitlab.arturbosch.detekt:detekt-cli:$version"))
			}
		}
	}

	companion object {
		private const val DETEKT = "detekt"
		private const val IDEA_FORMAT = "detektIdeaFormat"
		private const val IDEA_INSPECT = "detektIdeaInspect"
		private const val GENERATE_CONFIG = "detektGenerateConfig"
		private const val BASELINE = "detektBaseline"
	}
}

const val CONFIGURATION_DETEKT = "detekt"
const val CONFIGURATION_DETEKT_PLUGINS = "detektPlugins"
