package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Project
import org.gradle.api.plugins.quality.CodeQualityExtension
import java.io.File

/**
 * @author Artur Bosch
 * @author Said Tahsin Dane
 * @author Marvin Ramin
 */
open class DetektExtension(private val project: Project) : CodeQualityExtension() {
	var baseline: File? = null
	var config: File? = null
	var debug: Boolean = false
	var parallel: Boolean? = null
	var disableDefaultRuleSets: Boolean? = null
	var filters: String? = null
	var plugins: String? = null

	var ideaExtension: IdeaExtension = IdeaExtension()

	// TODO: ReportingExtension
}
