package io.gitlab.arturbosch.detekt

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Incubating
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.quality.CheckstyleReports
import org.gradle.api.plugins.quality.internal.CheckstyleReportsImpl
import org.gradle.api.reporting.Reporting
import org.gradle.api.resources.TextResource
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.VerificationTask
import java.io.File
import javax.inject.Inject

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
@CacheableTask
open class Detekt : SourceTask(), VerificationTask, Reporting<CheckstyleReports> {

	private val reports: CheckstyleReports = getObjectFactory().newInstance(CheckstyleReportsImpl::class.java, this)
	private var _ignoreFailures: Boolean = false
	open lateinit var classpath: FileCollection
	open var config: TextResource? = null
	open var filters: String? = null
	open var baseline: File? = null
	open var plugins: String? = null
	open var debug: Boolean = false
	open var parallel: Boolean = false
	open var disableDefaultRuleSets: Boolean = false

	/**
	 * Injects and returns an instance of [org.gradle.api.model.ObjectFactory].
	 *
	 * @since 4.2
	 */
	@Incubating
	@Inject
	open fun getObjectFactory(): ObjectFactory {
		throw UnsupportedOperationException()
	}

	@OutputFiles
	fun getOutputFiles(): Map<String, File> {
		val map = HashMap<String, File>()

		if (reports.xml.isEnabled) {
			map += "XML" to reports.xml.destination
		}
		if (reports.html.isEnabled) {
			map += "HTML" to reports.html.destination
		}
		return map
	}

	fun configureForSourceSet(sourceSet: SourceSet) {
		description = "Run detekt analysis for ${sourceSet.name} classes"
		classpath = sourceSet.compileClasspath
		setSource(sourceSet.allSource)
	}

	override fun getReports(): CheckstyleReports {
		return reports
	}

	override fun getIgnoreFailures() = _ignoreFailures
	override fun setIgnoreFailures(ignoreFailures: Boolean) {
		_ignoreFailures = ignoreFailures
	}

	override fun reports(closure: Closure<*>?) = reports(ClosureBackedAction<CheckstyleReports>(closure))
	override fun reports(configureAction: Action<in CheckstyleReports>?): CheckstyleReports {
		configureAction?.execute(reports)
		return reports
	}

	@TaskAction
	fun check() {
		DetektInvoker.check(this)
	}
}
