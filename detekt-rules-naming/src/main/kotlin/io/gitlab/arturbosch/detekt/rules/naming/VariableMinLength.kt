package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.calls.util.isSingleUnderscore

/**
 * Reports when very short variable names are used.
 *
 * @configuration minimumVariableNameLength - maximum name length (default: `1`)
 */
class VariableMinLength(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Variable names should not be shorter than the minimum defined in the configuration.",
        debt = Debt.FIVE_MINS
    )

    private val minimumVariableNameLength =
        valueOrDefault(MINIMUM_VARIABLE_NAME_LENGTH, DEFAULT_MINIMUM_VARIABLE_NAME_LENGTH)

    override fun visitProperty(property: KtProperty) {
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
