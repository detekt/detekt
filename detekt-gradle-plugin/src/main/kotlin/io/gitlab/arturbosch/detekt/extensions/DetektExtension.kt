package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.quality.CodeQualityExtension
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

	val reports = DetektReports(project)
	fun reports(configure: Action<DetektReports>) = configure.execute(reports)

	val idea = IdeaExtension()
	fun idea(configure: Action<IdeaExtension>) = configure.execute(idea)

	var input: ConfigurableFileCollection = project.layout.configurableFiles(DEFAULT_SRC_DIR_JAVA, DEFAULT_SRC_DIR_KOTLIN)

	var baseline: File? = null

	var config: ConfigurableFileCollection = project.layout.configurableFiles()

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

