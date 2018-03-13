package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.resources.TextResource
import org.gradle.api.tasks.Internal
import java.io.File

/**
 * @author Artur Bosch
 * @author Said Tahsin Dane
 * @author Marvin Ramin
 */
open class DetektExtension(val project: Project) : CodeQualityExtension() {
	open var debug: Boolean = DEFAULT_DEBUG_VALUE
	open var parallel: Boolean = false
	open var disableDefaultRuleSets: Boolean = false
	open var filters: String? = null
	open var baseline: File? = null
	open var plugins: String? = null
	open var ideaExtension: IdeaExtension = IdeaExtension()
	open lateinit var config: TextResource
	open lateinit var configDir: File

	fun idea(configuration: Action<in IdeaExtension>) {
		configuration.execute(ideaExtension)
	}

	/**
	 * The Detekt configuration file to use.
	 */
	@Internal
	fun getConfigFile(): File? {
		return config.asFile()
	}

	/**
	 * The Detekt configuration file to use.
	 */
	fun setConfigFile(configFile: File) {
		config = project.resources.text.fromFile(configFile)
	}
}
