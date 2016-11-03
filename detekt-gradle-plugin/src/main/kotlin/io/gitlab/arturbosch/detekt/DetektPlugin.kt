package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.cli.Main
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.task(mapOf("type" to DetektTask::class.java), "detekt")
		project.extensions.create("detekt", DetektConfig::class.java)
	}

}

open class DetektTask : DefaultTask() {

	@TaskAction
	fun detekt() {
		val input = project.projectDir.absolutePath
		val detektConfig = project.extensions.getByName("detekt") as DetektConfig
		val args = mutableListOf<String>()
		with(detektConfig) {
			input?.let { args.add("-p"); args.add(it) }
			config?.let { args.add("-c"); args.add(it) }
			filters?.let { args.add("-f"); args.add(it) }
			rulesets?.let { args.add("-r"); args.add(it) }
		}
		Main.main(args.toTypedArray())
	}
}

open class DetektConfig(var message: String = "World",
						var config: String? = null,
						var filters: String? = null,
						var rulesets: String? = null)