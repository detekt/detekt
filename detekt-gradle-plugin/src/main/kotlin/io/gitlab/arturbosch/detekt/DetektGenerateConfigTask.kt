package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.GenerateConfigArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektGenerateConfigTask : DefaultTask() {

	init {
		description = "Generate a detekt configuration file inside your project."
		group = "verification"
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	lateinit var input: FileCollection

	@TaskAction
	fun generateConfig() {
		val arguments = mutableListOf<CliArgument>(GenerateConfigArgument()) +
				InputArgument(input)

		DetektInvoker.invokeCli(project, arguments.toList())
	}
}
