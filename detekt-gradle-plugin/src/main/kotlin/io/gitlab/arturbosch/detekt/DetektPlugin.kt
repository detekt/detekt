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
import java.io.File

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
		configureTaskDefaults()
	}

	private fun configureTaskDefaults() {
		project.tasks.configureEachLater(Detekt::class.java) {
			configureTaskConventionMapping(this)
			configureReportsConventionMapping(this)
		}
	}

	protected fun createConfigurations() {
		val configuration = project.configurations.create(DETEKT.toLowerCase())
		configuration.isVisible = false
		configuration.isTransitive = true
		configuration.description = "The " + DETEKT + " libraries to be used for this project."
		configurePluginDependencies(configuration)
	}

	private fun configurePluginDependencies(configuration: Configuration) {
		configuration.defaultDependencies {
			val detektCli = DefaultExternalModuleDependency("io.gitlab.arturbosch.detekt",
					"detekt-cli",
					detektExtension.toolVersion)
			add(project.dependencies.create(detektCli))
		}
	}

	private fun createExtension() {
		detektExtension = project.extensions.create(DETEKT, DetektExtension::class.java, project)
		detektExtension.toolVersion = "1.0.0-GRADLE"

		generateConfigTask = project.tasks.create(GENERATE_CONFIG, DetektGenerateConfigTask::class.java)
		createBaselineTask = project.tasks.create(BASELINE, DetektCreateBaselineTask::class.java)
		ideaFormatTask = project.tasks.create(IDEA_FORMAT, DetektIdeaFormatTask::class.java)
		ideaInspectionTask = project.tasks.create(IDEA_INSPECT, DetektIdeaInspectionTask::class.java)
	}

	private fun configureExtensionRule() {
		val extensionMapping = conventionMappingOf(detektExtension)
		extensionMapping.map("reportsDir") {
			project.extensions.getByType(ReportingExtension::class.java).file(getReportName())
		}
		withBasePlugin(Action { extensionMapping.map("sourceSets") { getJavaPluginConvention().sourceSets } })
	}

	private fun getReportName() = DETEKT.toLowerCase()

	private fun configureReportsConventionMapping(task: Detekt) {
		task.reports.all {
			val reportMapping = conventionMappingOf(this)
			reportMapping.map("enabled") {
				withDetektExtensionFallingBackToParent(project) { it.reports.withName(name)?.enabled } ?: true
			}
			reportMapping.map("destination") {
				val fileSuffix = name

				val reportsDir = withDetektExtensionFallingBackToParent(project) {
					it.reportsDir
				} ?: detektExtension.defaultReportsDir

				val customDestination = withDetektExtensionFallingBackToParent(project) {
					it.reports.withName(name)?.destination?.let { File(project.layout.projectDirectory.asFile, it) }
				}
				customDestination ?: File(reportsDir, "$DETEKT.$fileSuffix")
			}
		}
	}

	private fun configureTaskConventionMapping(task: Detekt) {
		val taskMapping = task.conventionMapping
		taskMapping.map("config") { withDetektExtensionFallingBackToParent(project) { it.config } }
		taskMapping.map("baseline") { withDetektExtensionFallingBackToParent(project) { it.baseline } }
		taskMapping.map("debug") { withDetektExtensionFallingBackToParent(project) { it.debug } }
		taskMapping.map("disableDefaultRuleSets") {
			withDetektExtensionFallingBackToParent(project) { it.disableDefaultRuleSets }
		}
		taskMapping.map("filters") { withDetektExtensionFallingBackToParent(project) { it.filters } }
		taskMapping.map("parallel") { withDetektExtensionFallingBackToParent(project) { it.parallel } }
		taskMapping.map("plugins") { withDetektExtensionFallingBackToParent(project) { it.plugins } }
	}

	/**
	 * Retrieve a value from the projects extension or fall back to extension from parent
	 */
	private fun <T> withDetektExtensionFallingBackToParent(project: Project?, retrieveValueFrom: (DetektExtension) -> T?): T? {
		if (project == null) return null
		val extension = project.extensions.findByType(DetektExtension::class.java)
		val projectValue = extension?.let { retrieveValueFrom(it) }
		return when {
			projectValue != null -> projectValue
			else -> withDetektExtensionFallingBackToParent(project.parent, retrieveValueFrom)
		}
	}

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
			val detektTaskName = getTaskName(DETEKT, null)
			project.tasks.createLater(detektTaskName, Detekt::class.java) { configureForSourceSet(sourceSet, this) }
		}
	}

	private fun configureCheckTask() {
		withBasePlugin(Action { configureCheckTaskDependency() })
	}

	private fun configureCheckTaskDependency() {
		project.tasks.getByNameLater(Task::class.java, JavaBasePlugin.CHECK_TASK_NAME).configure {
			dependsOn(DETEKT_TASK_NAME)
		}
	}

	fun withBasePlugin(action: Action<Plugin<*>>) {
		project.plugins.withType(JavaBasePlugin::class.java, action)
	}

	protected fun getJavaPluginConvention(): JavaPluginConvention {
		return project.convention.getPlugin(JavaPluginConvention::class.java)
	}


	companion object {
		private const val DETEKT = "detekt"
		private const val DETEKT_TASK_NAME = "detektMain"
		private const val IDEA_FORMAT = "detektIdeaFormat"
		private const val IDEA_INSPECT = "detektIdeaInspect"
		private const val GENERATE_CONFIG = "detektGenerateConfig"
		private const val BASELINE = "detektBaseline"

		private fun conventionMappingOf(anObject: Any): ConventionMapping {
			return (anObject as IConventionAware).conventionMapping
		}
	}
}
