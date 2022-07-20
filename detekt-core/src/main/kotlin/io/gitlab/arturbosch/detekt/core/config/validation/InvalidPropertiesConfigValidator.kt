package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.core.config.YamlConfig

internal class InvalidPropertiesConfigValidator(
    private val baseline: YamlConfig,
    private val deprecatedProperties: Set<String>,
    excludePatterns: Set<Regex>,
) : AbstractYamlConfigValidator(excludePatterns) {

    override fun validate(
        configToValidate: YamlConfig,
        settings: ValidationSettings
    ): Collection<Notification> {
        return testKeys(configToValidate.properties, baseline.properties, null, settings)
    }

    private fun testKeys(
        configToValidate: Map<String, Any>,
        baseline: Map<String, Any>,
        parentPath: String?,
        settings: ValidationSettings
    ): List<Notification> {
        val notifications = mutableListOf<Notification>()
        for (prop in configToValidate.keys) {
            val propertyPath = "${if (parentPath == null) "" else "$parentPath>"}$prop"
            val isExcluded = settings.excludePatterns.any { it.matches(propertyPath) }
            val isDeprecated = deprecatedProperties.contains(propertyPath)
            if (isExcluded || isDeprecated) {
                continue
            }
            notifications.addAll(
                checkProp(
                    propertyName = prop,
                    propertyPath = propertyPath,
                    configToValidate = configToValidate,
                    baseline = baseline,
                    settings = settings
                )
            )
        }
        return notifications
    }

    @Suppress("UNCHECKED_CAST")
    private fun checkProp(
        propertyName: String,
        propertyPath: String,
        configToValidate: Map<String, Any>,
        baseline: Map<String, Any>,
        settings: ValidationSettings
    ): List<Notification> {
        if (!baseline.contains(propertyName)) {
            return listOf(propertyDoesNotExists(propertyPath))
        }
        if (configToValidate[propertyName] is String && baseline[propertyName] is List<*>) {
            return listOf(propertyShouldBeAnArray(propertyPath, settings.warningsAsErrors))
        }

        val next = configToValidate[propertyName] as? Map<String, Any>
        val nextBase = baseline[propertyName] as? Map<String, Any>
        return when {
            next == null && nextBase != null ->
                listOf(nestedConfigurationExpected(propertyPath))
            baseline.contains(propertyName) && next != null && nextBase == null ->
                listOf(unexpectedNestedConfiguration(propertyPath))
            next != null && nextBase != null ->
                testKeys(next, nextBase, propertyPath, settings)
            else -> emptyList()
        }
    }

    companion object {

        internal fun propertyDoesNotExists(prop: String): Notification =
            SimpleNotification("Property '$prop' is misspelled or does not exist.")

        internal fun nestedConfigurationExpected(prop: String): Notification =
            SimpleNotification("Nested config expected for '$prop'.")

        internal fun unexpectedNestedConfiguration(prop: String): Notification =
            SimpleNotification("Unexpected nested config for '$prop'.")

        internal fun propertyShouldBeAnArray(
            prop: String,
            reportAsError: Boolean,
        ): Notification =
            SimpleNotification(
                "Property '$prop' should be a YAML array instead of a comma-separated String.",
                if (reportAsError) Notification.Level.Error else Notification.Level.Warning,
            )
    }
}
