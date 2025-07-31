package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isOperator
import dev.detekt.psi.isOverride
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
