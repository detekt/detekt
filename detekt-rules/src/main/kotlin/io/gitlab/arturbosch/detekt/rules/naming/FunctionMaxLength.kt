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
 * Reports when very long function names are used.
 *
 * @configuration maximumFunctionNameLength - maximum name length (default: `30`)
 */
class FunctionMaxLength(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Function names should not be longer than the maximum set in the project configuration.",
        debt = Debt.FIVE_MINS
    )

    private val maximumFunctionNameLength =
        valueOrDefault(MAXIMUM_FUNCTION_NAME_LENGTH, DEFAULT_MAXIMUM_FUNCTION_NAME_LENGTH)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.identifierName().length > maximumFunctionNameLength) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    message = "Function names should be at most $maximumFunctionNameLength characters long.")
            )
        }
    }

    companion object {
        const val MAXIMUM_FUNCTION_NAME_LENGTH = "maximumFunctionNameLength"
        private const val DEFAULT_MAXIMUM_FUNCTION_NAME_LENGTH = 30
    }
}
