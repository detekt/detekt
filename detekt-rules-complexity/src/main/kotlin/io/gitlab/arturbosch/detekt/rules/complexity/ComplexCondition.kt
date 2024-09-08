package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtWhileExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * Complex conditions make it hard to understand which cases lead to the condition being true or false. To improve
 * readability and understanding of complex conditions consider extracting them into well-named functions or variables
 * and call those instead.
 *
 * <noncompliant>
 * val str = "foo"
 * val isFoo = if (str.startsWith("foo") && !str.endsWith("foo") && !str.endsWith("bar") && !str.endsWith("_")) {
 *     // ...
 * }
 * </noncompliant>
 *
 * <compliant>
 * val str = "foo"
 * val isFoo = if (str.startsWith("foo") && hasCorrectEnding()) {
 *     // ...
 * }
 *
 * fun hasCorrectEnding() = return !str.endsWith("foo") && !str.endsWith("bar") && !str.endsWith("_")
 * </compliant>
 */
@ActiveByDefault(since = "1.0.0")
class ComplexCondition(config: Config) : Rule(
    config,
    "Complex conditions should be simplified and extracted into well-named methods if necessary."
) {

    @Configuration("Maximum allowed number of conditions.")
    private val allowedConditions: Int by config(defaultValue = 3)

    override fun visitIfExpression(expression: KtIfExpression) {
        val condition = expression.condition
        checkIfComplex(condition)
        super.visitIfExpression(expression)
    }

    override fun visitDoWhileExpression(expression: KtDoWhileExpression) {
        val condition = expression.condition
        checkIfComplex(condition)
        super.visitDoWhileExpression(expression)
    }

    override fun visitWhileExpression(expression: KtWhileExpression) {
        val condition = expression.condition
        checkIfComplex(condition)
        super.visitWhileExpression(expression)
    }

    private fun checkIfComplex(condition: KtExpression?) {
        val binaryExpressions = condition?.collectDescendantsOfType<KtBinaryExpression>() ?: return

        if (binaryExpressions.size > 1) {
            val longestBinExpr = binaryExpressions.reduce { acc, binExpr ->
                if (binExpr.text.length > acc.text.length) binExpr else acc
            }
            val conditionString = longestBinExpr.text
            val count = frequency(conditionString, "&&") + frequency(conditionString, "||") + 1
            if (count > allowedConditions) {
                report(
                    CodeSmell(
                        Entity.from(condition),
                        "This condition is too complex ($count). " +
                            "The defined maximum number of allowed conditions is set to '$allowedConditions'"
                    )
                )
            }
        }
    }

    private fun frequency(source: String, part: String): Int {
        if (source.isEmpty() || part.isEmpty()) {
            return 0
        }

        var count = 0
        var pos = source.indexOf(part, 0)
        while (pos != -1) {
            pos += part.length
            count++
            pos = source.indexOf(part, pos)
        }

        return count
    }
}
