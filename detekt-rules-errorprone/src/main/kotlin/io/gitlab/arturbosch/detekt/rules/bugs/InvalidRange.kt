package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression

/**
 * Reports ranges which are empty.
 * This might be a bug if it is used for instance as a loop condition. This loop will never be triggered then.
 * This might be due to invalid ranges like (10..9) which will cause the loop to never be entered.
 *
 * <noncompliant>
 * for (i in 2..1) {}
 * for (i in 1 downTo 2) {}
 *
 * val range1 = 2 until 1
 * val range2 = 2 until 2
 * </noncompliant>
 *
 * <compliant>
 * for (i in 2..2) {}
 * for (i in 2 downTo 2) {}
 *
 * val range = 2 until 3
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
class InvalidRange(config: Config) : Rule(
    config,
    "If a for loops condition is false before the first iteration, the loop will never get executed."
) {

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        if (expression.isInvalidLoopRange()) {
            report(CodeSmell(Entity.from(expression), "This loop will never be executed due to its expression."))
        }
        super.visitBinaryExpression(expression)
    }

    private fun KtBinaryExpression.isInvalidLoopRange(): Boolean {
        val lower = (left as? KtConstantExpression)?.text?.toIntOrNull() ?: return false
        val upper = (right as? KtConstantExpression)?.text?.toIntOrNull() ?: return false
        return when (operationReference.text) {
            ".." -> lower > upper
            "downTo" -> lower < upper
            "until", "..<" -> lower >= upper
            else -> false
        }
    }
}
