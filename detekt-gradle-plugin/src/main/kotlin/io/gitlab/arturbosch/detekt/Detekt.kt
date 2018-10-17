package io.gitlab.arturbosch.detekt

import groovy.lang.Closure
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
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.ReportingExtension
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
import org.gradle.util.ConfigureUtil
import java.io.File

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
	var baseline: RegularFileProperty = createNewInputFile()

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
	@Input
	var disableDefaultRuleSets: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

	@Internal
	var reports = DetektReports(project)

	fun reports(closure: Closure<*>): DetektReports = ConfigureUtil.configure(closure, reports)
	fun reports(configure: DetektReports.() -> Unit) = reports.configure()

	@Internal
	@Optional
	var reportsDir: Property<File> = project.objects.property()

	val xmlReportFile: Provider<RegularFile>
		@OutputFile
		@Optional
		get() = reports.xml.getTargetFileProvider(effectiveReportsDir)

	val htmlReportFile: Provider<RegularFile>
		@OutputFile
		@Optional
		get() = reports.html.getTargetFileProvider(effectiveReportsDir)

	private val defaultReportsDir: Directory = project.layout.buildDirectory.get()
			.dir(ReportingExtension.DEFAULT_REPORTS_DIR_NAME)
			.dir("detekt")

	private val effectiveReportsDir = project.provider { reportsDir.getOrElse(defaultReportsDir.asFile) }

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

	private fun createNewInputFile() = newInputFile()
}
