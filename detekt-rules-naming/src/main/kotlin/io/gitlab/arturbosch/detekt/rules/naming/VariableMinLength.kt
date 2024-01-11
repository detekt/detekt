package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.identifierName
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.calls.util.isSingleUnderscore

/**
 * Reports when very short variable names are used.
 */
class VariableMinLength(config: Config) : Rule(
    config,
    "Variable names should not be shorter than the minimum defined in the configuration."
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

        if (property.identifierName().length < minimumVariableNameLength) {
            report(
                CodeSmell(
                    issue,
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
