package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Reports when very short function names are used.
 */
class FunctionMinLength(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Function names should not be shorter than the minimum defined in the configuration.",
        debt = Debt.FIVE_MINS
    )

    override val defaultRuleIdAliases: Set<String>
        get() = setOf("FunctionMinNameLength")

    @Configuration("minimum name length")
    private val minimumFunctionNameLength: Int by config(3)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.identifierName().length < minimumFunctionNameLength) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    message = "Function names should be at least $minimumFunctionNameLength characters long."
                )
            )
        }
    }
}
