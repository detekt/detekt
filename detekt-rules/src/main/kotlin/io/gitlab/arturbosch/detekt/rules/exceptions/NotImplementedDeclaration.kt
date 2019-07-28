package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getCalleeExpressionIfAny

/**
 * This rule reports all exceptions of the type `NotImplementedError` that are thrown. It also reports all `TODO(..)`
 * functions.
 * These indicate that functionality is still under development and will not work properly. Both of these should only
 * serve as temporary declarations and should not be put into production environments.
 *
 * <noncompliant>
 * fun foo() {
 *     throw NotImplementedError()
 * }
 *
 * fun todo() {
 *     TODO("")
 * }
 * </noncompliant>
 */
class NotImplementedDeclaration(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("NotImplementedDeclaration", Severity.CodeSmell,
            "The NotImplementedDeclaration should only be used when a method stub is necessary. " +
                    "This defers the development of the functionality of this function. " +
                    "Hence, the NotImplementedDeclaration should only serve as a temporary declaration. " +
                    "Before releasing, this type of declaration should be removed.",
            Debt.TWENTY_MINS)

    override fun visitThrowExpression(expression: KtThrowExpression) {
        val calleeExpression = expression.thrownExpression?.getCalleeExpressionIfAny()
        if (calleeExpression?.text == "NotImplementedError") {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        if (expression.calleeExpression?.text == "TODO") {
            val size = expression.valueArguments.size
            if (size == 0 || size == 1) {
                report(CodeSmell(issue, Entity.from(expression), issue.description))
            }
        }
    }
}
