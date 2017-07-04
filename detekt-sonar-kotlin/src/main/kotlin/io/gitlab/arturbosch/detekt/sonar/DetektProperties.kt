package io.gitlab.arturbosch.detekt.sonar

import org.sonar.api.config.PropertyDefinition
import org.sonar.api.resources.Qualifiers

/**
 * @author Artur Bosch
 */

const val CONFIG_PATH_KEY = "detekt.sonar.kotlin.config.path"
const val CONFIG_PATH_DESCRIPTION = "Path to the detekt yaml config." +
		" Path may be absolute or relative to the project base directory."
const val CONFIG_PATH_DEFAULT = ""

const val CONFIG_RESOURCE_KEY = "detekt.sonar.kotlin.config.resource"
const val CONFIG_RESOURCE_DESCRIPTION = "Resource name of the detekt yaml config."
const val CONFIG_RESOURCE_DEFAULT = ""

const val PATH_FILTERS_KEY = "detekt.sonar.kotlin.filters"
const val PATH_FILTERS_DESCRIPTIONS = "Regex based path filters eg. '.*/test/.*'"
const val PATH_FILTERS_DEFAULTS = ".*/test/.*,.*/resources/.*,.*/build/.*"

val PROPERTIES = listOf<PropertyDefinition>(
		PropertyDefinition.builder(CONFIG_PATH_KEY)
				.name("Detekt yaml config path")
				.defaultValue(CONFIG_PATH_DEFAULT)
				.description(CONFIG_PATH_DESCRIPTION)
				.onQualifiers(listOf(Qualifiers.PROJECT))
				.build(),
		PropertyDefinition.builder(CONFIG_RESOURCE_KEY)
				.name("Detekt yaml resource config")
				.defaultValue(CONFIG_RESOURCE_DEFAULT)
				.description(CONFIG_RESOURCE_DESCRIPTION)
				.onQualifiers(listOf(Qualifiers.PROJECT))
				.build(),
		PropertyDefinition.builder(PATH_FILTERS_KEY)
				.name("Detekt path filters")
				.defaultValue(PATH_FILTERS_DEFAULTS)
				.description(PATH_FILTERS_DESCRIPTIONS)
				.onQualifiers(listOf(Qualifiers.PROJECT))
				.build()
)