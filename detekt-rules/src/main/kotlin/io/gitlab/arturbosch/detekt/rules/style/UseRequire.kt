package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.arguments
import io.gitlab.arturbosch.detekt.rules.isEmptyOrSingleStringArgument
import io.gitlab.arturbosch.detekt.rules.isEnclosedByConditionalStatement
import io.gitlab.arturbosch.detekt.rules.isIllegalArgumentException
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * Kotlin provides a much more concise way to check preconditions than to manually throw an
 * IllegalArgumentException.
 *
 * <noncompliant>
 * if (value == null) throw IllegalArgumentException("value should not be null")
 * if (value < 0) throw IllegalArgumentException("value is $value but should be at least 0")
 * </noncompliant>
 *
 * <compliant>
 * requireNotNull(value) { "value should not be null" }
 * require(value >= 0) { "value is $value but should be at least 0" }
 * </compliant>
 */
class UseRequire(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "UseRequire", Severity.Style,
        "Use require() instead of throwing an IllegalArgumentException.",
        Debt.FIVE_MINS
    )

    override fun visitThrowExpression(expression: KtThrowExpression) {
        if (!expression.isIllegalArgumentException()) return

        if (expression.isOnlyExpressionInBlock()) return

        if (expression.isEnclosedByConditionalStatement() &&
            expression.arguments.isEmptyOrSingleStringArgument(bindingContext)) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    private fun KtThrowExpression.isOnlyExpressionInBlock(): Boolean {
        return when (val p = parent) {
            is KtBlockExpression -> p.statements.size == 1
            is KtNamedFunction -> true
            else -> false
        }
    }
}
