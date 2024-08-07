package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtExpression

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
 */
@ActiveByDefault(since = "1.0.0")
class UnreachableCode(config: Config) :
    Rule(
        config,
        "Unreachable code detected. This code should be removed."
    ),
    RequiresTypeResolution {
    override fun visitExpression(expression: KtExpression) {
        super.visitExpression(expression)
        if (bindingContext.diagnostics.forElement(expression)
                .any { it.factory == Errors.UNREACHABLE_CODE || it.factory == Errors.USELESS_ELVIS }
        ) {
            report(
                CodeSmell(
                    Entity.from(expression),
                    "This expression is unreachable code which should either be used or removed."
                )
            )
        }
    }
}
