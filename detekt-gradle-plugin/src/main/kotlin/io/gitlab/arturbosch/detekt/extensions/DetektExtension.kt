package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Project
import org.gradle.api.plugins.quality.CodeQualityExtension
import java.io.File

/**
 * @author Artur Bosch
 * @author Said Tahsin Dane
 * @author Marvin Ramin
 */
open class DetektExtension(project: Project) : CodeQualityExtension() {
	var baseline: File? = null
	var config: File? = null
	var debug: Boolean = false
	var parallel: Boolean? = null
	var disableDefaultRuleSets: Boolean? = null
	var filters: String? = null
	var plugins: String? = null

	val reports = project.extensions.create("reports", DetektReportsExtension::class.java, project)

	var ideaExtension: IdeaExtension = IdeaExtension()
}

open class DetektReportsExtension(project: Project) {
	val xml = project.extensions.create("xml", DetektReportExtension::class.java, project)
	val html = project.extensions.create("html", DetektReportExtension::class.java, project)
	fun withName(name: String) = when (name.toLowerCase()) {
		"xml" -> xml
		"html" -> html
		else -> throw IllegalArgumentException("name '${name}' is not a supported report name")
	}
}

open class DetektReportExtension(project: Project) {
	var enabled: Boolean = true
	var destination: File? = null
}
