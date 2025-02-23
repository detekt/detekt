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
 * Reports when very short function names are used.
 */
@Alias("FunctionMinNameLength")
class FunctionNameMinLength(config: Config) : Rule(
    config,
    "Function names should not be shorter than the minimum set in detekt's configuration."
) {

    @Configuration("minimum name length")
    private val minimumFunctionNameLength: Int by config(3)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.isOverride() || function.isOperator()) {
            return
        }
        val functionName = function.name ?: return

        if (functionName.length < minimumFunctionNameLength) {
            report(
                Finding(
                    Entity.atName(function),
                    message = "Function names should be at least $minimumFunctionNameLength characters long."
                )
            )
        }
    }
}
