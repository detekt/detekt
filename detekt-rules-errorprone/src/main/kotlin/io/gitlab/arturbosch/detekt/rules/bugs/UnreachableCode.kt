package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Reports unreachable code.
 * Code can be unreachable because it is behind return, throw, continue or break expressions.
 * This unreachable code should be removed as it serves no purpose.
 *
 * <noncompliant>
 * for (i in 1..2) {
 *     break
 *     println() // unreachable
 * }
 *
 * throw IllegalArgumentException()
 * println() // unreachable
 *
 * fun f() {
 *     return
 *     println() // unreachable
 * }
 * </noncompliant>
 *
 * @active since v1.0.0
 * @requiresTypeResolution
 */
class UnreachableCode(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("UnreachableCode", Severity.Warning,
            "Unreachable code detected. This code should be removed", Debt.TEN_MINS)

    override fun visitExpression(expression: KtExpression) {
        super.visitExpression(expression)
        if (bindingContext != BindingContext.EMPTY &&
            bindingContext.diagnostics.forElement(expression).any { it.factory == Errors.UNREACHABLE_CODE }
        ) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "This expression is unreachable code which should either be used or removed."
                )
            )
        }
    }
}
