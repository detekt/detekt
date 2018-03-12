package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.plugins.quality.internal.AbstractCodeQualityPlugin
import org.gradle.api.tasks.SourceSet
import java.io.File

/**
 * @author Marvin Ramin
 */
class DetektPlugin : AbstractCodeQualityPlugin<Detekt>() {

	private val detektExtension: DetektExtension
		get() = extension as DetektExtension

	private lateinit var generateConfigTask: DetektGenerateConfigTask
	private lateinit var createBaselineTask: DetektCreateBaselineTask
	private lateinit var ideaFormatTask: DetektIdeaFormatTask
	private lateinit var ideaInspectionTask: DetektIdeaInspectionTask

	override fun getToolName() = DETEKT_EXTENSION_NAME
	override fun getTaskType() = Detekt::class.java

	override fun createExtension(): CodeQualityExtension {
		val extension = project.extensions.create(DETEKT, DetektExtension::class.java, project)
		extension.toolVersion = "1.0.0.RC6-MARVIN"
		extension.configDir = project.rootProject.file("detekt-cli/src/main/resources/")
		extension.config = project.resources.text.fromFile {
			File(extension.configDir, "default-detekt-config.yml")
		}

		generateConfigTask = project.task(mapOf(org.gradle.api.Task.TASK_TYPE to DetektGenerateConfigTask::class.java), GENERATE_CONFIG) as DetektGenerateConfigTask
		createBaselineTask = project.task(mapOf(org.gradle.api.Task.TASK_TYPE to DetektCreateBaselineTask::class.java), BASELINE) as DetektCreateBaselineTask
		ideaFormatTask = project.task(mapOf(org.gradle.api.Task.TASK_TYPE to DetektIdeaFormatTask::class.java), IDEA_FORMAT) as DetektIdeaFormatTask
		ideaInspectionTask = project.task(mapOf(org.gradle.api.Task.TASK_TYPE to DetektIdeaInspectionTask::class.java), IDEA_INSPECT) as DetektIdeaInspectionTask

		return extension
	}

	override fun configureForSourceSet(sourceSet: SourceSet, task: Detekt) {
		task.configureForSourceSet(sourceSet)

		generateConfigTask.detekt = task
		createBaselineTask.detekt = task
		ideaFormatTask.detekt = task
		ideaFormatTask.ideaExtension = detektExtension.ideaExtension
		ideaInspectionTask.detekt = task
		ideaInspectionTask.ideaExtension = detektExtension.ideaExtension
	}

	override fun configureTaskDefaults(task: Detekt, baseName: String) {
		val configuration = project.configurations.getAt(DETEKT)
		configureDefaultDependencies(configuration)
		configureTaskConventionMapping(configuration, task)
		configureReportsConventionMapping(task, baseName)
	}

	private fun configureTaskConventionMapping(configuration: Configuration, task: Detekt) {
		val taskMapping = task.conventionMapping
		taskMapping.map("config") { detektExtension.config }
		taskMapping.map("filters") { detektExtension.filters }
		taskMapping.map("baseline") { detektExtension.baseline }
		taskMapping.map("debug") { detektExtension.debug }
		taskMapping.map("parallel") { detektExtension.parallel }
		taskMapping.map("plugins") { detektExtension.plugins }
		taskMapping.map("disableDefaultRuleSets") { detektExtension.disableDefaultRuleSets }
	}

	private fun configureReportsConventionMapping(task: Detekt, baseName: String) {
		task.reports.all { report ->
			val reportMapping = conventionMappingOf(report)
			reportMapping.map("enabled") { true }
			reportMapping.map("destination") { File(extension.reportsDir, "$baseName.${report.name}") }
		}
	}

	private fun configureDefaultDependencies(configuration: Configuration) {
		configuration.defaultDependencies {
			val detektCli = DefaultExternalModuleDependency("io.gitlab.arturbosch.detekt",
					"detekt-cli",
					detektExtension.toolVersion)
			it.add(project.dependencies.create(detektCli))
		}
	}

	override fun beforeApply() {
		// Default Tasks
//		project.task(mapOf(org.gradle.api.Task.TASK_DEPENDS_ON to "detekt"), LEGACY_DETEKT_TASK)
//		project.task(mapOf(org.gradle.api.Task.TASK_TYPE to DetektIdeaFormatTask::class.java), IDEA_FORMAT)
//		project.task(mapOf(org.gradle.api.Task.TASK_TYPE to DetektIdeaInspectionTask::class.java), IDEA_INSPECT)
	}

	companion object {
		private const val DETEKT_EXTENSION_NAME = "detekt"
		private const val DETEKT = "detekt"
		private const val LEGACY_DETEKT_TASK = "detektCheck"
		private const val IDEA_FORMAT = "detektIdeaFormat"
		private const val IDEA_INSPECT = "detektIdeaInspect"
		private const val GENERATE_CONFIG = "detektGenerateConfig"
		private const val BASELINE = "detektBaseline"
	}
}
