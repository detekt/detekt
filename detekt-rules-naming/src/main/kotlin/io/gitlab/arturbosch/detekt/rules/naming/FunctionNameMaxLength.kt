package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isOperator
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Reports when very long function names are used.
 */
@Alias("FunctionMaxNameLength")
class FunctionNameMaxLength(config: Config) : Rule(
    config,
    "Function names should not be longer than the maximum set in detekt's configuration."
) {

    @Configuration("maximum name length")
    private val maximumFunctionNameLength: Int by config(DEFAULT_MAXIMUM_FUNCTION_NAME_LENGTH)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.isOverride() || function.isOperator()) {
            return
        }
        val functionName = function.name ?: return

        if (functionName.length > maximumFunctionNameLength) {
            report(
                Finding(
                    Entity.atName(function),
                    message = "Function names should be at most $maximumFunctionNameLength characters long."
                )
            )
        }
    }

    companion object {
        const val DEFAULT_MAXIMUM_FUNCTION_NAME_LENGTH = 30
    }
}
