package dev.detekt.rules.naming

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
