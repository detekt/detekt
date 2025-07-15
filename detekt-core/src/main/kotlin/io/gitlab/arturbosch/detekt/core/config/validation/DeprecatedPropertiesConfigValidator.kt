package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import io.gitlab.arturbosch.detekt.core.util.SimpleNotification

internal class DeprecatedPropertiesConfigValidator(
    private val deprecatedProperties: Set<DeprecatedProperty>,
) : AbstractYamlConfigValidator() {

    override val id: String = "DeprecatedPropertiesConfigValidator"

    override fun validate(
        configToValidate: YamlConfig,
        settings: ValidationSettings,
    ): Collection<Notification> {
        val configAsMap = configToValidate.properties
        return deprecatedProperties
            .filter { hasValue(configAsMap, it) }
            .map { createNotification(it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun hasValue(configAsMap: Map<String, Any>, deprecatedProperty: DeprecatedProperty): Boolean {
        val ruleSetSubMap = configAsMap[deprecatedProperty.ruleSetId] as? Map<String, Any> ?: return false
        val ruleSubMap = ruleSetSubMap[deprecatedProperty.ruleName] as? Map<String, Any> ?: return false
        return ruleSubMap.containsKey(deprecatedProperty.propertyName)
    }

    private fun createNotification(
        foundProperty: DeprecatedProperty,
    ): Notification {
        val propertyPath = foundProperty.asPath()
        return SimpleNotification(
            "Property '$propertyPath' is deprecated. ${foundProperty.description}.",
            Notification.Level.Warning,
        )
    }

    private fun DeprecatedProperty.asPath() = "$ruleSetId>$ruleName>$propertyName"
}
