package io.gitlab.arturbosch.detekt.sonar.foundation

import org.sonar.api.resources.AbstractLanguage

/**
 * @author Artur Bosch
 */
class KotlinLanguage : AbstractLanguage(KOTLIN_KEY, KOTLIN_NAME) {

	override fun getFileSuffixes(): Array<String>
			= arrayOf(KOTLIN_FILE_SUFFIX, KOTLIN_SCRIPT_SUFFIX)

}