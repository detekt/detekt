package io.gitlab.arturbosch.detekt

import groovy.lang.Closure
import io.gitlab.arturbosch.detekt.invoke.*
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.reporting.Reporting
import org.gradle.api.tasks.*
import org.gradle.util.ConfigureUtil
import java.io.File

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
@CacheableTask
open class Detekt : DefaultTask(), Reporting<DetektReports> {

	private val _reports: DetektReports = project.objects.newInstance(DetektReportsImpl::class.java, this)
	@Internal
	override fun getReports() = _reports

	override fun reports(closure: Closure<*>): DetektReports = ConfigureUtil.configure(closure, _reports)
	override fun reports(configureAction: Action<in DetektReports>): DetektReports = _reports.apply { configureAction.execute(this) }

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
	var debugOrDefault: Boolean = false

	@Internal
	@Optional
	var parallelOrDefault: Boolean = false

	@Internal
	@Optional
	var disableDefaultRuleSetsOrDefault: Boolean = false

	val xmlReportFile: File?
		@OutputFile
		@Optional
		get() = if (reports.xml.isEnabled) reports.xml.destination else null

	val htmlReportFile: File?
		@OutputFile
		@Optional
		get() = if (reports.html.isEnabled) reports.html.destination else null

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
				DebugArgument(debugOrDefault) +
				ParallelArgument(parallelOrDefault) +
				DisableDefaultRulesetArgument(disableDefaultRuleSetsOrDefault)

		DetektInvoker.invokeCli(project, arguments.toList(), debugOrDefault)
	}
}
