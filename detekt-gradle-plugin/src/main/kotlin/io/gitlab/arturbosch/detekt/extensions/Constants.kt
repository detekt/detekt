package io.gitlab.arturbosch.detekt.extensions

/**
 * @author Artur Bosch
 */

const val DETEKT_PROFILE = "detekt.profile"
const val SUPPORTED_DETEKT_VERSION = "latest.release"
const val DEFAULT_DEBUG_VALUE = false

const val DEFAULT_PROFILE_NAME = "main"
const val DEFAULT_TRUE = "true"
const val DEFAULT_DETEKT_CONFIG_RESOURCE = "/default-detekt-config.yml"
const val DEFAULT_PATH_EXCLUDES = ".*/resources/.*,.*/build/.*,.*/target/.*"

const val DEBUG_PARAMETER = "--debug"
const val CONFIG_RESOURCE_PARAMETER = "--config-resource"
const val FILTERS_PARAMETER = "--filters"
const val INPUT_PARAMETER = "--input"
const val CONFIG_PARAMETER = "--config"
const val RULES_PARAMETER = "--rules"
const val OUTPUT_PARAMETER = "--output"
const val OUTPUT_NAME_PARAMETER = "--output-name"
const val BASELINE_PARAMETER = "--baseline"
const val PARALLEL_PARAMETER = "--parallel"
const val DISABLE_DEFAULT_RULESETS_PARAMETER = "--disable-default-rulesets"
const val PLUGINS_PARAMETER = "--plugins"
