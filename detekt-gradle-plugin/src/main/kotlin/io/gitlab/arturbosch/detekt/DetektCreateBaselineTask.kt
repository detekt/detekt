package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 * @author Olivier Lemasle
 */
open class DetektCreateBaselineTask : DefaultTask() {

	private val classpath: FileCollection

	init {
		description = "Creates a detekt baseline on the given --baseline path."
		group = "verification"

		val detektExtension = project.extensions.getByName("detekt") as DetektExtension
		classpath = detektExtension.resolveClasspath(project)
		dependsOn(classpath)
	}

	private val createBaseline = "--create-baseline"

	@TaskAction
	fun baseline() {
		val detektExtension = project.extensions.getByName("detekt") as DetektExtension

		project.javaexec {
			it.main = "io.gitlab.arturbosch.detekt.cli.Main"
			it.classpath = classpath
			it.args(detektExtension.resolveArguments(project).plus(createBaseline))
		}
	}

}
