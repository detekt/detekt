package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektReports
import io.gitlab.arturbosch.detekt.invoke.BaselineArgument
import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.DebugArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.DisableDefaultRulesetArgument
import io.gitlab.arturbosch.detekt.invoke.FiltersArgument
import io.gitlab.arturbosch.detekt.invoke.HtmlReportArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import io.gitlab.arturbosch.detekt.invoke.ParallelArgument
import io.gitlab.arturbosch.detekt.invoke.PluginsArgument
import io.gitlab.arturbosch.detekt.invoke.XmlReportArgument
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author Markus Schwarz
 */
@CacheableTask
open class Detekt : DefaultTask() {

	init {
		group = LifecycleBasePlugin.VERIFICATION_GROUP
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	var input: ConfigurableFileCollection = project.layout.configurableFiles()

	@Input
	@Optional
	var filters: Property<String> = project.objects.property()

	@InputFile
	@Optional
	@PathSensitive(PathSensitivity.RELATIVE)
	var baseline: RegularFileProperty = project.layout.fileProperty()

	@InputFiles
	@Optional
	@PathSensitive(PathSensitivity.RELATIVE)
	var config: ConfigurableFileCollection = project.layout.configurableFiles()

	@Input
	@Optional
	var plugins: Property<String> = project.objects.property()

	@Internal
	@Optional
	var debug: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

	@Internal
	@Optional
	var parallel: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

	@Internal
	@Optional
	var disableDefaultRuleSets: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

	@OutputFile
	@Optional
	var xmlReportFile: RegularFileProperty = project.layout.fileProperty()

	@OutputFile
	@Optional
	var htmlReportFile: RegularFileProperty = project.layout.fileProperty()

	fun setReportFileProvider(name: String, provider: Provider<RegularFile>) {
		when (name) {
			DetektReports.XML_REPORT_NAME -> xmlReportFile.set(provider)
			DetektReports.HTML_REPORT_NAME -> htmlReportFile.set(provider)
		}
	}

	@TaskAction
	fun check() {
		val arguments = mutableListOf<CliArgument>() +
				InputArgument(input) +
				FiltersArgument(filters.orNull) +
				ConfigArgument(config) +
				PluginsArgument(plugins.orNull) +
				BaselineArgument(baseline.orNull) +
				XmlReportArgument(xmlReportFile.orNull) +
				HtmlReportArgument(htmlReportFile.orNull) +
				DebugArgument(debug.get()) +
				ParallelArgument(parallel.get()) +
				DisableDefaultRulesetArgument(disableDefaultRuleSets.get())

		DetektInvoker.invokeCli(project, arguments.toList(), debug.get())
	}
}
