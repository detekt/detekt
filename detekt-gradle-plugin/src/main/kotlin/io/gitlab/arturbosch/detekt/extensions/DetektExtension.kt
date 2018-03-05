package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Project
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.resources.TextResource
import java.io.File

/**
 * @author Artur Bosch
 * @author Said Tahsin Dane
 * @author Marvin Ramin
 */
open class DetektExtension(val project: Project) : CodeQualityExtension() {

	open var version: String = SUPPORTED_DETEKT_VERSION
	open var debug: Boolean = DEFAULT_DEBUG_VALUE
	open var parallel: Boolean = false
	open var disableDefaultRuleSets: Boolean = false
	open var filters: String? = null
	open var baseline: File? = null
	open var plugins: String? = null
	open var ideaExtension: IdeaExtension = IdeaExtension()
	open lateinit var config: TextResource
	open lateinit var configDir: File
//	fun ideaFormatArgs() = ideaExtension.formatArgs(this)
//	fun ideaInspectArgs() = ideaExtension.inspectArgs(this)
}
