package io.gitlab.arturbosch.detekt

import groovy.lang.Closure
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.reporting.Reporting
import org.gradle.api.resources.TextResource
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.property
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
) : SourceTask(), VerificationTask, Reporting<DetektReports> {

	private val reports: DetektReports = objectFactory.newInstance(DetektReportsImpl::class.java, this)
	private var _ignoreFailures: Boolean = false
	lateinit var classpath: FileCollection
	open var configProperty: Property<TextResource?> = objectFactory.property()
	open var filtersProperty: Property<String?> = objectFactory.property()
	open var baselineProperty: Property<File?> = objectFactory.property()
	open var pluginsProperty: Property<String?> = objectFactory.property()
	open var debugProperty: Property<Boolean?> = objectFactory.property()
	open var parallelProperty: Property<Boolean?> = objectFactory.property()
	open var disableDefaultRuleSetsProperty: Property<Boolean?> = objectFactory.property()

	var config: TextResource?
		get() = configProperty.orNull
		set(value) = configProperty.set(value)

	var filters: String?
		get() = filtersProperty.orNull
		set(value) = filtersProperty.set(value)

	var plugins: String?
		get() = pluginsProperty.orNull
		set(value) = pluginsProperty.set(value)

	var baseline: File?
		get() = baselineProperty.orNull
		set(value) = baselineProperty.set(value)

	var debug: Boolean?
		get() = debugProperty.orNull
		set(value) = debugProperty.set(value)

	var parallel: Boolean?
		get() = parallelProperty.orNull
		set(value) = parallelProperty.set(value)

	var disableDefaultRuleSets: Boolean?
		get() = disableDefaultRuleSetsProperty.orNull
		set(value) = disableDefaultRuleSetsProperty.set(value)

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
		group = "verification"
		classpath = sourceSet.compileClasspath
		setSource(sourceSet.allSource)
	}

	override fun getReports() = reports

	override fun getIgnoreFailures() = _ignoreFailures
	override fun setIgnoreFailures(ignoreFailures: Boolean) {
		_ignoreFailures = ignoreFailures
	}

	override fun reports(closure: Closure<*>) = reports(ClosureBackedAction<DetektReports>(closure))
	override fun reports(configureAction: Action<in DetektReports>?): DetektReports {
		configureAction?.execute(reports)
		return reports
	}

	@TaskAction
	fun check() {
		DetektInvoker.check(this)
	}

}
