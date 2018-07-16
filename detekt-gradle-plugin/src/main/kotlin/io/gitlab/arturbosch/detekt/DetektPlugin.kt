package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.ConventionMapping
import org.gradle.api.internal.IConventionAware
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * @author Marvin Ramin
 */
class DetektPlugin : Plugin<Project> {

	private lateinit var project: Project
	private lateinit var detektExtension: DetektExtension
	private lateinit var generateConfigTask: DetektGenerateConfigTask
	private lateinit var createBaselineTask: DetektCreateBaselineTask
	private lateinit var ideaFormatTask: DetektIdeaFormatTask
	private lateinit var ideaInspectionTask: DetektIdeaInspectionTask

	override fun apply(p: Project) {
		project = p
		project.pluginManager.apply(ReportingBasePlugin::class.java)

		createConfigurations()
		createExtension()
		configureExtensionRule()
		configureSourceSetRule()
		configureCheckTask()
	}

	protected fun createConfigurations() {
		val configuration = project.configurations.create(DETEKT.toLowerCase())
		configuration.isVisible = false
		configuration.isTransitive = true
		configuration.description = "The " + DETEKT + " libraries to be used for this project."
		// Don't need these things, they're provided by the runtime
		configuration.exclude(excludeProperties("ant", "ant"))
		configuration.exclude(excludeProperties("org.apache.ant", "ant"))
		configuration.exclude(excludeProperties("org.apache.ant", "ant-launcher"))
		configuration.exclude(excludeProperties("org.slf4j", "slf4j-api"))
		configuration.exclude(excludeProperties("org.slf4j", "jcl-over-slf4j"))
		configuration.exclude(excludeProperties("org.slf4j", "log4j-over-slf4j"))
		configuration.exclude(excludeProperties("commons-logging", "commons-logging"))
		configuration.exclude(excludeProperties("log4j", "log4j"))
		configureConfiguration(configuration)
	}

	private fun configureConfiguration(configuration: Configuration) {
		configureDefaultDependencies(configuration)
	}

	private fun configureDefaultDependencies(configuration: Configuration) {
		configuration.defaultDependencies {
			val detektCli = DefaultExternalModuleDependency("io.gitlab.arturbosch.detekt",
					"detekt-cli",
					detektExtension.toolVersion)
			add(project.dependencies.create(detektCli))
		}
	}

	private fun excludeProperties(group: String, module: String) = mapOf("group" to group, "module" to module)


	private fun createExtension() {
		detektExtension = project.extensions.create(DETEKT, DetektExtension::class.java, project)
		detektExtension.toolVersion = "latest.release"

		generateConfigTask = project.tasks.create(GENERATE_CONFIG, DetektGenerateConfigTask::class.java)
		createBaselineTask = project.tasks.create(BASELINE, DetektCreateBaselineTask::class.java)
		ideaFormatTask = project.tasks.create(IDEA_FORMAT, DetektIdeaFormatTask::class.java)
		ideaInspectionTask = project.tasks.create(IDEA_INSPECT, DetektIdeaInspectionTask::class.java)
	}

	private fun configureExtensionRule() {
		val extensionMapping = conventionMappingOf(detektExtension)
		extensionMapping.map("sourceSets") { emptyList<SourceSet>() }
		extensionMapping.map("reportsDir") { project.extensions.getByType(ReportingExtension::class.java).file(getReportName()) }
		withBasePlugin(Action { extensionMapping.map("sourceSets") { getJavaPluginConvention().sourceSets } })
	}

	private fun getReportName() = DETEKT.toLowerCase()

	fun configureForSourceSet(sourceSet: SourceSet, task: Detekt) {
		task.configureForSourceSet(sourceSet)

		generateConfigTask.detekt = task
		createBaselineTask.detekt = task
		ideaFormatTask.detekt = task
		ideaFormatTask.ideaExtension = detektExtension.ideaExtension
		ideaInspectionTask.detekt = task
		ideaInspectionTask.ideaExtension = detektExtension.ideaExtension
	}

	private fun configureSourceSetRule() {
		withBasePlugin(Action { configureForSourceSets(getJavaPluginConvention().getSourceSets()) })
	}

	private fun configureForSourceSets(sourceSets: SourceSetContainer) {
		sourceSets.all {
			val sourceSet = this
			project.tasks.createLater(getTaskName(DETEKT, null), Detekt::class.java) {
				configureForSourceSet(sourceSet, this)
			}
		}
	}

	private fun configureCheckTask() {
		withBasePlugin(Action { configureCheckTaskDependents() })
	}

	private fun configureCheckTaskDependents() {
		val taskBaseName = DETEKT.toLowerCase()
		project.tasks.getByNameLater(Task::class.java, JavaBasePlugin.CHECK_TASK_NAME).configure {
			val dependencies = detektExtension.sourceSets.map { it.getTaskName(taskBaseName, null) }
			dependsOn(dependencies)
			println("################################## $dependencies")
		}
	}

	fun withBasePlugin(action: Action<Plugin<*>>) {
		project.plugins.withType(JavaBasePlugin::class.java, action)
	}

	protected fun getJavaPluginConvention(): JavaPluginConvention {
		return project.convention.getPlugin(JavaPluginConvention::class.java)
	}


	companion object {
		private const val DETEKT_EXTENSION_NAME = "detekt"
		private const val DETEKT = "detekt"
		private const val LEGACY_DETEKT_TASK = "detektCheck"
		private const val IDEA_FORMAT = "detektIdeaFormat"
		private const val IDEA_INSPECT = "detektIdeaInspect"
		private const val GENERATE_CONFIG = "detektGenerateConfig"
		private const val BASELINE = "detektBaseline"

		private fun conventionMappingOf(anObject: Any): ConventionMapping {
			return (anObject as IConventionAware).conventionMapping
		}
	}
}
