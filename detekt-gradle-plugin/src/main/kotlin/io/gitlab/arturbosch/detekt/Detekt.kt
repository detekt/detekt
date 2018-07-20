package io.gitlab.arturbosch.detekt

import groovy.lang.Closure
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.reporting.Reporting
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.invoke
import org.gradle.util.ConfigureUtil
import java.io.File
import javax.inject.Inject

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
@CacheableTask
open class Detekt
@Inject
constructor(
		objectFactory: ObjectFactory
) : ConventionTask(), VerificationTask, Reporting<DetektReports> {

	private val _reports: DetektReports = objectFactory.newInstance(DetektReportsImpl::class.java, this)
	@Internal
	override fun getReports() = _reports

	override fun reports(closure: Closure<*>): DetektReports = ConfigureUtil.configure(closure, _reports)
	override fun reports(configureAction: Action<in DetektReports>): DetektReports = _reports.apply { configureAction(this) }

	private var _ignoreFailures: Boolean = false
	@Optional
	@Input
	override fun getIgnoreFailures() = _ignoreFailures

	override fun setIgnoreFailures(ignoreFailures: Boolean) {
		_ignoreFailures = ignoreFailures
	}

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
	var debug: Boolean? = null

	@Internal
	@Optional
	var parallel: Boolean? = null

	@Internal
	@Optional
	var disableDefaultRuleSets: Boolean? = null

	@OutputFiles
	fun getOutputFiles(): Map<String, File> {
		val map = HashMap<String, File>()

		if (reports.xml.isEnabled) {
			map += "XML" to _reports.xml.destination
		}
		if (reports.html.isEnabled) {
			map += "HTML" to _reports.html.destination
		}
		return map
	}

	fun configureForSourceSet(sourceSet: SourceSet) {
		description = "Run detekt analysis for ${sourceSet.name} classes"
		group = "verification"
		input = if (sourceSet.allSource.asPath.isBlank()) project.files() else sourceSet.java.sourceDirectories
	}

	@TaskAction
	fun check() {
		DetektInvoker.check(this)
	}
}
