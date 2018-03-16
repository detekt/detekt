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
		val extension = project.extensions.create(DETEKT, DetektExtension::class.java, project, project.objects, project.layout)
		extension.toolVersion = System.getProperty("detektVersion")
		extension.configDir.set(project.rootProject.file("detekt-cli/src/main/resources/"))
		extension.config.set(project.resources.text.fromFile {
			File(extension.configDir.get().asFile, "default-detekt-config.yml")
		})

		generateConfigTask = project.tasks.create(GENERATE_CONFIG, DetektGenerateConfigTask::class.java)
		createBaselineTask = project.tasks.create(BASELINE, DetektCreateBaselineTask::class.java)
		ideaFormatTask = project.tasks.create(IDEA_FORMAT, DetektIdeaFormatTask::class.java)
		ideaInspectionTask = project.tasks.create(IDEA_INSPECT, DetektIdeaInspectionTask::class.java)

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
		configureTask(task)
		configureReportsConventionMapping(task, baseName)
	}

	private fun configureTask(task: Detekt) {
		task.config.set(detektExtension.config)
		task.filters.set(detektExtension.filters)
		task.baseline.set(detektExtension.baseline)
		task.debug.set(detektExtension.debug)
		task.parallel.set(detektExtension.parallel)
		task.plugins.set(detektExtension.plugins)
		task.disableDefaultRuleSets.set(detektExtension.disableDefaultRuleSets)
	}

	private fun configureReportsConventionMapping(task: Detekt, baseName: String) {
		task.reports.forEach { report ->
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
			add(project.dependencies.create(detektCli))
		}
	}

	override fun beforeApply() {
		// Default Tasks
		project.task(mapOf(org.gradle.api.Task.TASK_DEPENDS_ON to "detektMain"), LEGACY_DETEKT_TASK)
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
