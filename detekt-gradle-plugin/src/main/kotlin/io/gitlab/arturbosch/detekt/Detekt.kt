package io.gitlab.arturbosch.detekt

import groovy.lang.Closure
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.DetektReport
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
import org.gradle.api.file.FileCollection
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
import org.gradle.util.ConfigureUtil
import java.io.File

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author Markus Schwarz
 */
@CacheableTask
open class Detekt : DefaultTask() {

	@Internal
	val reports = DetektReports()

	fun reports(closure: Closure<*>) = ConfigureUtil.configure(closure, reports)
	fun reports(configure: DetektReports.() -> Unit) = reports.configure()

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
	var baseline: File? = null

	@InputFile
	@Optional
	@PathSensitive(PathSensitivity.ABSOLUTE)
	var config: File? = null

	@Input
	@Optional
	var plugins: String? = null

	@Internal
	@Optional
	var debug: Boolean = DetektExtension.DEFAULT_DEBUG_VALUE

	@Internal
	@Optional
	var parallel: Boolean = DetektExtension.DEFAULT_PARALLEL_VALUE

	@Internal
	@Optional
	var disableDefaultRuleSets: Boolean = DetektExtension.DEFAULT_DISABLE_RULESETS_VALUE

	val xmlReportFile: File?
		@OutputFile
		@Optional
		get() = getReportFile(reports.xml)

	val htmlReportFile: File?
		@OutputFile
		@Optional
		get() = getReportFile(reports.html)

	private fun getReportFile(report: DetektReport) = if (report.enabled) report.destination else null

	@TaskAction
	fun check() {
		val arguments = mutableListOf<CliArgument>() +
				InputArgument(input) +
				FiltersArgument(filters) +
				ConfigArgument(config) +
				PluginsArgument(plugins) +
				BaselineArgument(baseline) +
				XmlReportArgument(xmlReportFile) +
				HtmlReportArgument(htmlReportFile) +
				DebugArgument(debug) +
				ParallelArgument(parallel) +
				DisableDefaultRulesetArgument(disableDefaultRuleSets)

		DetektInvoker.invokeCli(project, arguments.toList(), debug)
	}
}
