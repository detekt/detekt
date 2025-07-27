package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.calls.util.isSingleUnderscore

/**
 * Reports when very short variable names are used.
 */
class VariableMinLength(config: Config) : Rule(
    config,
    "Variable names should not be shorter than the minimum set in detekt's configuration."
) {

    @Configuration("minimum name length")
    private val minimumVariableNameLength: Int by config(DEFAULT_MINIMUM_VARIABLE_NAME_LENGTH)

    override fun visitProperty(property: KtProperty) {
        if (property.isOverride()) {
            return
        }

        if (property.isSingleUnderscore) {
            return
        }

        val propertyName = property.name ?: return

        if (propertyName.length < minimumVariableNameLength) {
            report(
                Finding(
                    Entity.atName(property),
                    message = "Variable names should be at least $minimumVariableNameLength characters long."
                )
            )
        }
    }

    companion object {
        const val MINIMUM_VARIABLE_NAME_LENGTH = "minimumVariableNameLength"
        private const val DEFAULT_MINIMUM_VARIABLE_NAME_LENGTH = 1
    }
}
