package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Reports when very short function names are used.
 *
 * @configuration minimumFunctionNameLength - minimum name length (default: `3`)
 */
class FunctionMinLength(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Function names should not be shorter than the minimum defined in the configuration.",
        debt = Debt.FIVE_MINS
    )

    private val minimumFunctionNameLength =
        valueOrDefault(MINIMUM_FUNCTION_NAME_LENGTH, DEFAULT_MINIMUM_FUNCTION_NAME_LENGTH)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.identifierName().length < minimumFunctionNameLength) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    message = "Function names should be at least $minimumFunctionNameLength characters long.")
            )
        }
    }

    companion object {
        const val MINIMUM_FUNCTION_NAME_LENGTH = "minimumFunctionNameLength"
        private const val DEFAULT_MINIMUM_FUNCTION_NAME_LENGTH = 3
    }
}
