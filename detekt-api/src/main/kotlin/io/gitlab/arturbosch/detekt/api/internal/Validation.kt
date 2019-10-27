package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.CompositeConfig
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.YamlConfig

private val identifierRegex = Regex("[aA-zZ]+([-][aA-zZ]+)*")

/**
 * Checks if given string matches the criteria of an id - [aA-zZ]+([-][aA-zZ]+)* .
 */
internal fun validateIdentifier(id: String) {
    require(id.matches(identifierRegex)) { "id must match [aA-zZ]+([-][aA-zZ]+)*" }
}

@Suppress("UNCHECKED_CAST", "ComplexMethod")
fun validateConfig(config: Config, baseline: Config): List<Notification> {
    require(baseline != Config.empty) { "Cannot validate configuration based on an empty baseline config." }
    require(baseline is YamlConfig) { "Only supported baseline config is the YamlConfig." }

    if (config == Config.empty) {
        return emptyList()
    }

    val notifications = mutableListOf<Notification>()

    fun testKeys(current: Map<String, Any>, base: Map<String, Any>, parentPath: String?) {
        for (prop in current.keys) {

            val propertyPath = "${if (parentPath == null) "" else "$parentPath>"}$prop"
            if (!base.contains(prop)) {
                notifications.add(doesNotExistsMessage(propertyPath))
            }

            val next = current[prop] as? Map<String, Any>
            val nextBase = base[prop] as? Map<String, Any>

            when {
                next == null && nextBase != null -> notifications.add(nestedConfigExpectedMessage(propertyPath))
                base.contains(prop) && next != null && nextBase == null ->
                    notifications.add(unexpectedNestedConfigMessage(propertyPath))
                next != null && nextBase != null -> testKeys(next, nextBase, propertyPath)
            }
        }
    }

    when (config) {
        is YamlConfig -> testKeys(config.properties, baseline.properties, null)
        is CompositeConfig -> notifications.addAll(CompositeConfig.validate(config, baseline))
        else -> error("Unsupported config type for validation: '${config::class}'.")
    }

    return notifications
}

internal fun doesNotExistsMessage(prop: String): Notification =
    SimpleNotification("Property '$prop' is misspelled or does not exist.")

internal fun nestedConfigExpectedMessage(prop: String): Notification =
    SimpleNotification("Nested config expected for '$prop'.")

internal fun unexpectedNestedConfigMessage(prop: String): Notification =
    SimpleNotification("Unexpected nested config for '$prop'.")
