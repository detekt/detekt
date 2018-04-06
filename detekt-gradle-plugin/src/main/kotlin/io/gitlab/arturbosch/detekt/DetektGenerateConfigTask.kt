package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 * @author Olivier Lemasle
 */
open class DetektGenerateConfigTask : DefaultTask() {

	private val classpath: FileCollection

	init {
		description = "Generate a detekt configuration file inside your project."
		group = "verification"

		val detektExtension = project.extensions.getByName("detekt") as DetektExtension
		classpath = detektExtension.resolveClasspath(project)
		dependsOn(classpath)
	}

	@TaskAction
	fun generateConfig() {
		project.javaexec {
			it.main = "io.gitlab.arturbosch.detekt.cli.Main"
			it.classpath = classpath
			it.args("--input", project.projectDir.absolutePath, "--generate-config")
		}
	}
}
