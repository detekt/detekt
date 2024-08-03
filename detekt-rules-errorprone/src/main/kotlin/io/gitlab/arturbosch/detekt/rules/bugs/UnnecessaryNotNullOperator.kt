package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtUnaryExpression

/**
 * Reports unnecessary not-null operator usage (!!) that can be removed by the user.
 *
 * <noncompliant>
 * val a = 1
 * val b = a!!
 * </noncompliant>
 *
 * <compliant>
 * val a = 1
 * val b = a
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class UnnecessaryNotNullOperator(config: Config) :
    Rule(
        config,
        "Unnecessary not-null unary operator (!!) detected."
    ),
    RequiresTypeResolution {
    override fun visitUnaryExpression(expression: KtUnaryExpression) {
        super.visitUnaryExpression(expression)

        val compilerReports = bindingContext.diagnostics.forElement(expression.operationReference)
        if (compilerReports.any { it.factory == Errors.UNNECESSARY_NOT_NULL_ASSERTION }) {
            report(
                CodeSmell(
                    Entity.from(expression),
                    "${expression.text} contains an unnecessary " +
                        "not-null (!!) operators"
                )
            )
        }
    }
}
