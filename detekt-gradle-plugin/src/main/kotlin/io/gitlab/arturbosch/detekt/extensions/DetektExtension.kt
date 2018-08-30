package io.gitlab.arturbosch.detekt.extensions

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.reporting.ReportingExtension
import org.gradle.util.ConfigureUtil
import java.io.File

/**
 * @author Artur Bosch
 * @author Said Tahsin Dane
 * @author Marvin Ramin
 * @author Markus Schwarz
 */
open class DetektExtension(project: Project) : CodeQualityExtension() {
	val defaultReportsDir = project.layout.buildDirectory.get()
			.dir(ReportingExtension.DEFAULT_REPORTS_DIR_NAME)
			.dir("detekt").asFile

	val reports = DetektReports()
	fun reports(configure: DetektReports.() -> Unit) = reports.configure()
	fun reports(configure: Closure<*>) = ConfigureUtil.configure(configure, reports)


	val idea = IdeaExtension()
	fun idea(configure: IdeaExtension.() -> Unit) = idea.configure()
	fun idea(configure: Closure<*>) = ConfigureUtil.configure(configure, idea)

	var input: FileCollection = project.files(DEFAULT_SRC_DIR_JAVA, DEFAULT_SRC_DIR_KOTLIN)

	var baseline: File? = null

	var config: File? = null

	var debug: Boolean = DEFAULT_DEBUG_VALUE

	var parallel: Boolean = DEFAULT_PARALLEL_VALUE

	var disableDefaultRuleSets: Boolean = DEFAULT_DISABLE_RULESETS_VALUE

	var filters: String? = null

	var plugins: String? = null

	companion object {
		const val DEFAULT_SRC_DIR_JAVA = "src/main/java"
		const val DEFAULT_SRC_DIR_KOTLIN = "src/main/kotlin"
		const val DEFAULT_DEBUG_VALUE = false
		const val DEFAULT_PARALLEL_VALUE = false
		const val DEFAULT_DISABLE_RULESETS_VALUE = false
		const val DEFAULT_REPORT_ENABLED_VALUE = true
	}
}

