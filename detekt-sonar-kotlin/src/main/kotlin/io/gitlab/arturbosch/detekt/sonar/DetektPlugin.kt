package io.gitlab.arturbosch.detekt.sonar

import org.sonar.api.Plugin

/**
 * @author Artur Bosch
 */
class DetektPlugin : Plugin {

	override fun define(context: Plugin.Context) {
		context.addExtensions(listOf(
				KotlinLanguage::class.java,
				KotlinProfile::class.java,
				DetektSensor::class.java,
				DetektRulesDefinition::class.java
		))
	}

}