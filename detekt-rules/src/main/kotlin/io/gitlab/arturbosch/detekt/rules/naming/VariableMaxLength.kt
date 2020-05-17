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

/**
 * Reports when very long variable names are used.
 *
 * @configuration maximumVariableNameLength - maximum name length (default: `64`)
 */
class VariableMaxLength(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Variable names should not be longer than the maximum set in the configuration.",
        debt = Debt.FIVE_MINS
    )

    private val maximumVariableNameLength =
        valueOrDefault(MAXIMUM_VARIABLE_NAME_LENGTH, DEFAULT_MAXIMUM_VARIABLE_NAME_LENGTH)

    override fun visitProperty(property: KtProperty) {
        if (property.identifierName().length > maximumVariableNameLength) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(property),
                    message = "Variable names should be at most $maximumVariableNameLength characters long."
                )
            )
        }
    }

    companion object {
        const val MAXIMUM_VARIABLE_NAME_LENGTH = "maximumVariableNameLength"
        private const val DEFAULT_MAXIMUM_VARIABLE_NAME_LENGTH = 64
    }
}
