package io.gitlab.arturbosch.detekt.api.internal

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

fun verifyConfig(config: Config, baseline: Config): List<Notification> {
    if (config == Config.empty && baseline == Config.empty) {
        return emptyList()
    }

    val notifications = mutableListOf<Notification>()

    @Suppress("UNCHECKED_CAST")
    fun testKeys(current: Map<String, Any>, base: Map<String, Any>, parentPath: String?) {
        for (prop in current.keys) {

            val propertyPath = "${if (parentPath == null) "" else "$parentPath>"}$prop"
            if (!base.contains(prop)) {
                notifications.add(doesNotExistsMessage(propertyPath))
            }

            val next = current[prop]?.let { it as? Map<String, Any> }
            val nextBase = base[prop]?.let { it as? Map<String, Any> }

            when {
                next == null && nextBase != null -> notifications.add(nestedConfigExpectedMessage(propertyPath))
                base.contains(prop) && next != null && nextBase == null ->
                    notifications.add(unexpectedNestedConfigMessage(propertyPath))
                next != null && nextBase != null -> testKeys(next, nextBase, propertyPath)
            }
        }
    }

    if (config is YamlConfig && baseline is YamlConfig) {
        testKeys(config.properties, baseline.properties, null)
    }

    return notifications
}

internal fun doesNotExistsMessage(prop: String): Notification =
    SimpleNotification("Property '$prop' is misspelled or does not exist.")

internal fun nestedConfigExpectedMessage(prop: String): Notification =
    SimpleNotification("Nested config expected for '$prop'.")

internal fun unexpectedNestedConfigMessage(prop: String): Notification =
    SimpleNotification("Unexpected nested config for '$prop'.")
