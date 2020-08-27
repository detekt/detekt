package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification

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

private val DEPRECATED_PROPERTIES = setOf(
    "complexity>LongParameterList>threshold" to "Use 'functionThreshold' and 'constructorThreshold' instead",
    "empty-blocks>EmptyFunctionBlock>ignoreOverriddenFunctions" to "Use 'ignoreOverridden' instead",
    "naming>FunctionParameterNaming>ignoreOverriddenFunctions" to "Use 'ignoreOverridden' instead",
    "naming>MemberNameEqualsClassName>ignoreOverriddenFunction" to "Use 'ignoreOverridden' instead"
).map { (first, second) -> first.toRegex() to second }

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

    fun testKeys(current: Map<String, Any>, base: Map<String, Any>, parentPath: String?) {
        for (prop in current.keys) {

            val propertyPath = "${if (parentPath == null) "" else "$parentPath>"}$prop"

            val deprecationWarning = DEPRECATED_PROPERTIES
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
