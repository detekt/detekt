package io.gitlab.arturbosch.detekt.extensions

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.provider.Provider
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

	val customReportsDir: File?
		get() = reportsDir

	private val defaultReportsDir: Directory = project.layout.buildDirectory.get()
			.dir(ReportingExtension.DEFAULT_REPORTS_DIR_NAME)
			.dir("detekt")

	val reports = DetektReports(project)
	fun reports(configure: DetektReports.() -> Unit) = reports.configure()
	fun reports(configure: Closure<*>): DetektReports = ConfigureUtil.configure(configure, reports)


	val idea = IdeaExtension()
	fun idea(configure: IdeaExtension.() -> Unit) = idea.configure()
	fun idea(configure: Closure<*>): IdeaExtension = ConfigureUtil.configure(configure, idea)

	var input: ConfigurableFileCollection = project.layout.configurableFiles(DEFAULT_SRC_DIR_JAVA, DEFAULT_SRC_DIR_KOTLIN)

	var baseline: File? = null

	var config: ConfigurableFileCollection = project.layout.configurableFiles()

	var debug: Boolean = DEFAULT_DEBUG_VALUE

	var parallel: Boolean = DEFAULT_PARALLEL_VALUE

	var disableDefaultRuleSets: Boolean = DEFAULT_DISABLE_RULESETS_VALUE

	var filters: String? = null

	var plugins: String? = null

	val reportsDirProvider: Provider<Directory> = project.provider({
		val dir = customReportsDir
		if (dir == null)
			defaultReportsDir
		else
			project.layout.projectDirectory.dir(dir.path)
	})

	companion object {
		const val DEFAULT_SRC_DIR_JAVA = "src/main/java"
		const val DEFAULT_SRC_DIR_KOTLIN = "src/main/kotlin"
		const val DEFAULT_DEBUG_VALUE = false
		const val DEFAULT_PARALLEL_VALUE = false
		const val DEFAULT_DISABLE_RULESETS_VALUE = false
		const val DEFAULT_REPORT_ENABLED_VALUE = true
	}
}

