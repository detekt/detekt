package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.GenerateConfigArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektGenerateConfigTask : DefaultTask() {

	init {
		description = "Generate a detekt configuration file inside your project."
		group = LifecycleBasePlugin.VERIFICATION_GROUP
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	lateinit var input: FileCollection

	@TaskAction
	fun generateConfig() {
		val arguments = mutableListOf<CliArgument>(GenerateConfigArgument) +
				InputArgument(input)

		DetektInvoker.invokeCli(project, arguments.toList())
	}
}
