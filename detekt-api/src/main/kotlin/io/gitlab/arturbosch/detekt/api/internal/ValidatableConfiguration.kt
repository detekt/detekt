package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.YamlConfig

interface ValidatableConfiguration {

    fun validate(baseline: Config, excludePatterns: Set<Regex>): List<Notification>
}

/**
 * Known existing properties on rule's which my be absent in the default-detekt-config.yml.
 *
 * We need to predefine them as the user may not have already declared an 'config'-block
 * in the configuration and we want to validate the config by default.
 */
val DEFAULT_PROPERTY_EXCLUDES = setOf(
    ".*>.*>excludes",
    ".*>.*>includes",
    ".*>.*>active",
    ".*>.*>autoCorrect",
    "build>weights.*"
).joinToString(",")

@Suppress("UNCHECKED_CAST", "ComplexMethod")
fun validateConfig(
    config: Config,
    baseline: Config,
    excludePatterns: Set<Regex> = CommaSeparatedPattern(DEFAULT_PROPERTY_EXCLUDES).mapToRegex()
): List<Notification> {
    require(baseline != Config.empty) { "Cannot validate configuration based on an empty baseline config." }
    require(baseline is YamlConfig) { "Only supported baseline config is the YamlConfig." }

    if (config == Config.empty) {
        return emptyList()
    }

    val notifications = mutableListOf<Notification>()

    fun testKeys(current: Map<String, Any>, base: Map<String, Any>, parentPath: String?) {
        for (prop in current.keys) {

            val propertyPath = "${if (parentPath == null) "" else "$parentPath>"}$prop"

            if (excludePatterns.any { it.matches(propertyPath) }) {
                continue
            }

            if (!base.contains(prop)) {
                notifications.add(propertyDoesNotExists(propertyPath))
            }

            val next = current[prop] as? Map<String, Any>
            val nextBase = base[prop] as? Map<String, Any>

            when {
                next == null && nextBase != null -> notifications.add(nestedConfigurationExpected(propertyPath))
                base.contains(prop) && next != null && nextBase == null ->
                    notifications.add(unexpectedNestedConfiguration(propertyPath))
                next != null && nextBase != null -> testKeys(next, nextBase, propertyPath)
            }
        }
    }

    when (config) {
        is YamlConfig -> testKeys(config.properties, baseline.properties, null)
        is ValidatableConfiguration -> notifications.addAll(config.validate(baseline, excludePatterns))
        else -> error("Unsupported config type for validation: '${config::class}'.")
    }

    return notifications
}

internal fun propertyDoesNotExists(prop: String): Notification =
    SimpleNotification("Property '$prop' is misspelled or does not exist.")

internal fun nestedConfigurationExpected(prop: String): Notification =
    SimpleNotification("Nested config expected for '$prop'.")

internal fun unexpectedNestedConfiguration(prop: String): Notification =
    SimpleNotification("Unexpected nested config for '$prop'.")
