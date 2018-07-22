package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.invoke.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
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
	var baseline: Property<File> = project.objects.property(File::class.java)

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	val input: Property<FileCollection> = project.objects.property(FileCollection::class.java)

	@Input
	@Optional
	var filters: Property<String> = project.objects.property(String::class.java)

	@InputFile
	@Optional
	@PathSensitive(PathSensitivity.ABSOLUTE)
	var config: Property<File> = project.objects.property(File::class.java)

	@Input
	@Optional
	val plugins: Property<String?> = project.objects.property(String::class.java)

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
				BaselineArgument(baseline.get()) +
				InputArgument(input.get()) +
				FiltersArgument(filters.orNull) +
				ConfigArgument(config.orNull) +
				PluginsArgument(plugins.orNull) +
				DebugArgument(debugOrDefault) +
				ParallelArgument(parallelOrDefault) +
				DisableDefaultRulesetArgument(disableDefaultRuleSetsOrDefault)

		DetektInvoker.invokeCli(project, arguments.toList(), debugOrDefault)
	}
}
