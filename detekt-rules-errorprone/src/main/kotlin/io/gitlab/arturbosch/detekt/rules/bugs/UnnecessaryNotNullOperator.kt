package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.resolve.BindingContext

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
 *
 * @requiresTypeResolution
 */
class UnnecessaryNotNullOperator(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue("UnnecessaryNotNullOperator",
            Severity.Defect,
            "Unnecessary not-null unary operator (!!) detected.",
            Debt.FIVE_MINS)

    override fun visitUnaryExpression(expression: KtUnaryExpression) {
        super.visitUnaryExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val compilerReports = bindingContext.diagnostics.forElement(expression.operationReference)
        if (compilerReports.any { it.factory == Errors.UNNECESSARY_NOT_NULL_ASSERTION }) {
            report(CodeSmell(issue, Entity.from(expression), "${expression.text} contains an unnecessary " +
                    "not-null (!!) operators"))
        }
    }
}
