package dev.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isOverride
import org.jetbrains.kotlin.psi.KtProperty

/**
 * Reports when very long variable names are used.
 */
class VariableMaxLength(config: Config) : Rule(
    config,
    "Variable names should not be longer than the maximum set in detekt's configuration."
) {

    @Configuration("maximum name length")
    private val maximumVariableNameLength: Int by config(DEFAULT_MAXIMUM_VARIABLE_NAME_LENGTH)

    override fun visitProperty(property: KtProperty) {
        if (property.isOverride()) {
            return
        }

        val propertyName = property.name ?: return

        if (propertyName.length > maximumVariableNameLength) {
            report(
                Finding(
                    Entity.atName(property),
                    message = "Variable names should be at most $maximumVariableNameLength characters long."
                )
            )
        }
    }

    companion object {
        private const val DEFAULT_MAXIMUM_VARIABLE_NAME_LENGTH = 64
    }
}
