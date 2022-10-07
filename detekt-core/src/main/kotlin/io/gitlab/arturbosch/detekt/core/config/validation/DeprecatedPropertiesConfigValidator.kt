package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.core.config.YamlConfig

internal class DeprecatedPropertiesConfigValidator(
    private val deprecatedProperties: Map<String, String>
) : AbstractYamlConfigValidator() {
    override fun validate(
        configToValidate: YamlConfig,
        settings: ValidationSettings
    ): Collection<Notification> {
        val configAsMap = configToValidate.properties
        return deprecatedProperties
            .map { (path, description) -> path.split(">") to description }
            .filter { (path, _) -> configAsMap.hasValue(path) }
            .map { (path, description) -> createNotification(path, description) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any>.hasValue(propertyPath: List<String>): Boolean {
        if (propertyPath.isEmpty()) {
            return false
        }
        if (propertyPath.size == 1) {
            return this.containsKey(propertyPath.first())
        }

        val subMap = this[propertyPath.first()] as? Map<String, Any> ?: return false
        return subMap.hasValue(propertyPath.drop(1))
    }

    private fun createNotification(
        propertyPath: List<String>,
        deprecationDescription: String
    ): Notification {
        val prop = propertyPath.joinToString(">")
        return SimpleNotification(
            "Property '$prop' is deprecated. $deprecationDescription.",
            Notification.Level.Warning
        )
    }
}
