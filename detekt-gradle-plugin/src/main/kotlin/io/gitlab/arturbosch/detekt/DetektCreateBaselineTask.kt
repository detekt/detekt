package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.invoke.BaselineArgument
import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.CreateBaselineArgument
import io.gitlab.arturbosch.detekt.invoke.DebugArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.DisableDefaultRulesetArgument
import io.gitlab.arturbosch.detekt.invoke.FiltersArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import io.gitlab.arturbosch.detekt.invoke.ParallelArgument
import io.gitlab.arturbosch.detekt.invoke.PluginsArgument
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektCreateBaselineTask : DefaultTask() {

	init {
		description = "Creates a detekt baseline on the given --baseline path."
		group = LifecycleBasePlugin.VERIFICATION_GROUP
	}

	@OutputFile
	@PathSensitive(PathSensitivity.RELATIVE)
	var baseline: File? = null

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	lateinit var input: FileCollection

	@Input
	@Optional
	var filters: String? = null

	@InputFiles
	@Optional
	@PathSensitive(PathSensitivity.RELATIVE)
	var config: FileCollection? = null

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
		val arguments = mutableListOf<CliArgument>(CreateBaselineArgument) +
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
