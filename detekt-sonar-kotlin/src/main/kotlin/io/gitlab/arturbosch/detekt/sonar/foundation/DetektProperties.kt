package io.gitlab.arturbosch.detekt.sonar.foundation

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
const val CONFIG_RESOURCE_DESCRIPTION = "Resource name of the detekt yaml config inside a custom rule set." +
		" Only one of the configurations will be chosen: ConfigPath > ConfigResource."
const val CONFIG_RESOURCE_DEFAULT = ""

const val PATH_FILTERS_KEY = "detekt.sonar.kotlin.filters"
const val PATH_FILTERS_DESCRIPTIONS = "Regex based path filters eg. '.*/test/.*'. " +
		"All paths like '/my/custom/test/path' will be filtered. If no filters are specified" +
		" defaults are: '.*/test/.*,.*/resources/.*,.*/build/.*,.*/target/.*'"
const val PATH_FILTERS_DEFAULTS = ".*/test/.*,.*/resources/.*,.*/build/.*,.*/target/.*"

const val QUALITY_PROFILES_KEY = "detekt.sonar.kotlin.quality.profile.paths"
const val QUALITY_PROFILES_DESCRIPTION = "Use absolute paths to detekt yaml configurations separated by ',' or ';'" +
		" to create quality profiles based on these configs. The 'Detekt way'-profile is always created and set to" +
		" default. It may be used as a reference configuration."
const val QUALITY_PROFILES_DEFAULT = ""

const val QUALITY_PROFILES_NAME = "Custom quality profile based on detekt configurations"
const val CONFIG_PATH_NAME = "Detekt yaml config path"
const val CONFIG_RESOURCE_NAME = "Detekt yaml resource config"
const val PATH_FILTERS_NAME = "Detekt path filters"

val PROPERTIES = listOf<PropertyDefinition>(
		PropertyDefinition.builder(QUALITY_PROFILES_KEY)
				.name(QUALITY_PROFILES_NAME)
				.defaultValue(QUALITY_PROFILES_DEFAULT)
				.description(QUALITY_PROFILES_DESCRIPTION)
				.build(),
		PropertyDefinition.builder(CONFIG_PATH_KEY)
				.name(CONFIG_PATH_NAME)
				.defaultValue(CONFIG_PATH_DEFAULT)
				.description(CONFIG_PATH_DESCRIPTION)
				.onQualifiers(listOf(Qualifiers.PROJECT))
				.build(),
		PropertyDefinition.builder(CONFIG_RESOURCE_KEY)
				.name(CONFIG_RESOURCE_NAME)
				.defaultValue(CONFIG_RESOURCE_DEFAULT)
				.description(CONFIG_RESOURCE_DESCRIPTION)
				.onQualifiers(listOf(Qualifiers.PROJECT))
				.build(),
		PropertyDefinition.builder(PATH_FILTERS_KEY)
				.name(PATH_FILTERS_NAME)
				.defaultValue(PATH_FILTERS_DEFAULTS)
				.description(PATH_FILTERS_DESCRIPTIONS)
				.onQualifiers(listOf(Qualifiers.PROJECT))
				.build()
)