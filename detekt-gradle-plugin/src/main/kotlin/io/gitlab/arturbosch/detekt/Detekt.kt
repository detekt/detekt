package io.gitlab.arturbosch.detekt

import groovy.lang.Closure
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.quality.CheckstyleReports
import org.gradle.api.plugins.quality.internal.CheckstyleReportsImpl
import org.gradle.api.provider.Property
import org.gradle.api.reporting.Reporting
import org.gradle.api.resources.TextResource
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.VerificationTask
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
		objectFactory: ObjectFactory,
		projectLayout: ProjectLayout
) : SourceTask(), VerificationTask, Reporting<CheckstyleReports> {

	private val reports: CheckstyleReports = objectFactory.newInstance(CheckstyleReportsImpl::class.java, this)
	private var _ignoreFailures: Boolean = false
	lateinit var classpath: FileCollection
	open var config: Property<TextResource> = objectFactory.property()
	open var filters: Property<String> = objectFactory.property()
	open var baseline: RegularFileProperty = projectLayout.fileProperty()
	open var plugins: Property<String> = objectFactory.property()
	open var debug: Property<Boolean> = objectFactory.property()
	open var parallel: Property<Boolean> = objectFactory.property()
	open var disableDefaultRuleSets: Property<Boolean> = objectFactory.property()

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

	/**
	 * The Detekt configuration file to use.
	 */
	@Internal
	fun getConfigFile(): File {
		return config.get().asFile()
	}

	/**
	 * The Detekt configuration file to use.
	 */
	fun setConfigFile(configFile: File) {
		config.set(project.resources.text.fromFile(configFile))
	}

	fun configureForSourceSet(sourceSet: SourceSet) {
		description = "Run detekt analysis for ${sourceSet.name} classes"
		group = "verification"
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

	override fun reports(closure: Closure<*>) = reports(ClosureBackedAction<CheckstyleReports>(closure))
	override fun reports(configureAction: Action<in CheckstyleReports>?): CheckstyleReports {
		configureAction?.execute(reports)
		return reports
	}

	@TaskAction
	fun check() {
		DetektInvoker.check(this)
	}
}
