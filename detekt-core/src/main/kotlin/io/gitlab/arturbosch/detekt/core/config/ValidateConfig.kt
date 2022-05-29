package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.utils.openSafeStream
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.internal.CommaSeparatedPattern
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import java.util.Properties

/**
 * Known existing properties on rule's which my be absent in the default-detekt-config.yml.
 *
 * We need to predefine them as the user may not have already declared an 'config'-block
 * in the configuration and we want to validate the config by default.
 */
val DEFAULT_PROPERTY_EXCLUDES = setOf(
    ".*>excludes",
    ".*>includes",
    ".*>active",
    ".*>.*>excludes",
    ".*>.*>includes",
    ".*>.*>active",
    ".*>.*>autoCorrect",
    ".*>severity",
    ".*>.*>severity",
    "build>weights.*",
    ".*>.*>ignoreAnnotated",
    ".*>.*>ignoreFunction",
).joinToString(",")

fun validateConfig(
    config: Config,
    baseline: Config,
    excludePatterns: Set<Regex> = CommaSeparatedPattern(DEFAULT_PROPERTY_EXCLUDES).mapToRegex()
): List<Notification> = validateConfig(
    config,
    baseline,
    ValidationSettings(
        config.subConfig("config").valueOrDefault("warningsAsErrors", false),
        excludePatterns,
    )
)

internal data class ValidationSettings(
    val warningsAsErrors: Boolean,
    val excludePatterns: Set<Regex>,
)

@Suppress("UNCHECKED_CAST", "ComplexMethod")
internal fun validateConfig(
    config: Config,
    baseline: Config,
    settings: ValidationSettings,
): List<Notification> {
    require(baseline != Config.empty) { "Cannot validate configuration based on an empty baseline config." }
    require(baseline is YamlConfig) {
        val yamlConfigClass = YamlConfig::class.simpleName
        val actualClass = baseline.javaClass.simpleName

        "Only supported baseline config is the $yamlConfigClass. Actual type is $actualClass"
    }

    if (config == Config.empty) {
        return emptyList()
    }

    val (warningsAsErrors, excludePatterns) = settings
    val notifications = mutableListOf<Notification>()

    fun getDeprecatedProperties(): List<Pair<Regex, String>> {
        return settings.javaClass.classLoader
            .getResource("deprecation.properties")!!
            .openSafeStream()
            .use { inputStream ->
                val prop = Properties().apply { load(inputStream) }

                prop.entries.map { entry ->
                    (entry.key as String).toRegex() to (entry.value as String)
                }
            }
    }

    fun testKeys(current: Map<String, Any>, base: Map<String, Any>, parentPath: String?) {
        for (prop in current.keys) {
            val propertyPath = "${if (parentPath == null) "" else "$parentPath>"}$prop"

            val deprecationWarning = getDeprecatedProperties()
                .find { (regex, _) -> regex.matches(propertyPath) }
                ?.second
            val isExcluded = excludePatterns.any { it.matches(propertyPath) }

            if (deprecationWarning != null) {
                notifications.add(propertyIsDeprecated(propertyPath, deprecationWarning, warningsAsErrors))
            }

            if (deprecationWarning != null || isExcluded) {
                continue
            }

            if (!base.contains(prop)) {
                notifications.add(propertyDoesNotExists(propertyPath))
            } else if (current[prop] is String && base[prop] is List<*>) {
                notifications.add(propertyShouldBeAnArray(propertyPath, warningsAsErrors))
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

internal fun propertyIsDeprecated(
    prop: String,
    deprecationDescription: String,
    reportAsError: Boolean,
): Notification =
    SimpleNotification(
        "Property '$prop' is deprecated. $deprecationDescription.",
        if (reportAsError) Notification.Level.Error else Notification.Level.Warning,
    )

internal fun propertyShouldBeAnArray(
    prop: String,
    reportAsError: Boolean,
): Notification =
    SimpleNotification(
        "Property '$prop' should be a YAML array instead of a comma-separated String.",
        if (reportAsError) Notification.Level.Error else Notification.Level.Warning,
    )
