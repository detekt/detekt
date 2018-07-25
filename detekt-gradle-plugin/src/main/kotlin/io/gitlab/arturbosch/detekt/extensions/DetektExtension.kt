package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.reporting.ReportingExtension
import java.io.File

/**
 * @author Artur Bosch
 * @author Said Tahsin Dane
 * @author Marvin Ramin
 * @author Markus Schwarz
 */
open class DetektExtension(private val project: Project) : CodeQualityExtension() {
	val defaultReportsDir = project.layout.buildDirectory.get()
			.dir(ReportingExtension.DEFAULT_REPORTS_DIR_NAME)
			.dir("detekt").asFile

	val reports = project.extensions.create("detektReports", DetektReportsExtension::class.java, project)
	fun reports(configure: DetektReportsExtension.() -> Unit) =
			project.extensions.configure(DetektReportsExtension::class.java, configure)


	val idea = project.extensions.create("detektIdea", IdeaExtension::class.java)
	fun idea(configure: IdeaExtension.() -> Unit) =
			project.extensions.configure(IdeaExtension::class.java, configure)

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

open class DetektReportsExtension(project: Project) {
	val xml = project.extensions.create("xml", DetektReportExtension::class.java)
	val html = project.extensions.create("html", DetektReportExtension::class.java)
	fun withName(name: String) = when (name.toLowerCase()) {
		"xml" -> xml
		"html" -> html
		else -> throw IllegalArgumentException("name '${name}' is not a supported report name")
	}
}

open class DetektReportExtension {

	var enabled: Boolean = DetektExtension.DEFAULT_REPORT_ENABLED_VALUE

	/**
	 * destination of the output - relative to the project root
	 */
	var destination: File? = null
}

