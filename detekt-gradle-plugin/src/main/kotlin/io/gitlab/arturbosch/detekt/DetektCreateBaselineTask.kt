package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.invoke.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import java.io.File

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektCreateBaselineTask : DefaultTask() {

	init {
		description = "Creates a detekt baseline on the given --baseline path."
		group = "verification"
	}

	@OutputFile
	@PathSensitive(PathSensitivity.ABSOLUTE)
	var baseline: File? = null

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	lateinit var input: FileCollection

	@Input
	@Optional
	var filters: String? = null

	@InputFile
	@Optional
	@PathSensitive(PathSensitivity.ABSOLUTE)
	var config: File? = null

	@Input
	@Optional
	var plugins: String? = null

	@Internal
	@Optional
	var debugOrDefault: Boolean = false

	@Internal
	@Optional
	var parallelOrDefault: Boolean = false

	@Internal
	@Optional
	var disableDefaultRuleSetsOrDefault: Boolean = false


	@TaskAction
	fun baseline() {
		val arguments = mutableListOf<CliArgument>(CreateBaselineArgument()) +
				BaselineArgument(baseline) +
				InputArgument(input) +
				FiltersArgument(filters) +
				ConfigArgument(config) +
				PluginsArgument(plugins) +
				DebugArgument(debugOrDefault) +
				ParallelArgument(parallelOrDefault) +
				DisableDefaultRulesetArgument(disableDefaultRuleSetsOrDefault)

		DetektInvoker.invokeCli(project, arguments.toList(), debugOrDefault)
	}
}
