package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File

/**
 * @author Marvin Ramin
 */
class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val extension = project.extensions.create(DETEKT, DetektExtension::class.java, project)

		configurePluginDependencies(project, extension)

		createDetektTasks(project, extension)
		createAndConfigureCreateBaselineTask(project, extension)
		createAndConfigureGenerateConfigTask(project, extension)

		createAndConfigureIdeaTasks(project, extension)
	}

	private fun createDetektTasks(project: Project, extension: DetektExtension) {
		project.sourceSets?.map { sourceSet ->
			val name = "$DETEKT${sourceSet.name.capitalize()}"
			val description = "Runs detekt on the ${sourceSet.name} source set."
			Detekt.create(project, extension, name, description, sourceSet.allSource)
		}

		val detektTask = Detekt.create(project, extension, DETEKT, "Runs the default detekt task.", determineInput(extension))
		project.tasks.findByName(LifecycleBasePlugin.CHECK_TASK_NAME)?.dependsOn(detektTask)
	}

	private fun createAndConfigureCreateBaselineTask(project: Project, extension: DetektExtension) =
			project.tasks.register(BASELINE, DetektCreateBaselineTask::class.java) {
				baseline = extension.baseline
				debugOrDefault = extension.debug
				parallelOrDefault = extension.parallel
				disableDefaultRuleSetsOrDefault = extension.disableDefaultRuleSets
				filters = extension.filters
				config = extension.config
				plugins = extension.plugins
				input = determineInput(extension)
			}

	private fun createAndConfigureGenerateConfigTask(project: Project, extension: DetektExtension) =
			project.tasks.register(GENERATE_CONFIG, DetektGenerateConfigTask::class.java) {
				input = determineInput(extension)
			}

	private fun createAndConfigureIdeaTasks(project: Project, extension: DetektExtension) {
		project.tasks.register(IDEA_FORMAT, DetektIdeaFormatTask::class.java) {
			debugOrDefault = extension.debug
			input = determineInput(extension)
			ideaExtension = extension.idea
		}

		project.tasks.register(IDEA_INSPECT, DetektIdeaInspectionTask::class.java) {
			debugOrDefault = extension.debug
			input = determineInput(extension)
			ideaExtension = extension.idea
		}
	}

	private fun determineInput(extension: DetektExtension) = extension.input.filter { it.exists() }

	private fun configurePluginDependencies(project: Project, extension: DetektExtension) {
		project.configurations.create(CONFIGURATION_DETEKT_PLUGINS) {
			isVisible = false
			isTransitive = true
			description = "The $CONFIGURATION_DETEKT_PLUGINS libraries to be used for this project."
		}

		project.configurations.create(CONFIGURATION_DETEKT) {
			isVisible = false
			isTransitive = true
			description = "The $CONFIGURATION_DETEKT dependencies to be used for this project."

			@Suppress("USELESS_ELVIS")
			val version = extension.toolVersion ?: DEFAULT_DETEKT_VERSION
			defaultDependencies {
				add(project.dependencies.create("io.gitlab.arturbosch.detekt:detekt-cli:$version"))
			}
		}
	}

	private val Project.sourceSets: SourceSetContainer? get() = project.getProperties()["sourceSets"] as? SourceSetContainer

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
