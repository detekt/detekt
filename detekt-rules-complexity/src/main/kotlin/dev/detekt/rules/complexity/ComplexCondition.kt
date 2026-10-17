package dev.detekt.rules.complexity

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.lexer.KtTokens
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
class ComplexCondition(config: Config) :
    Rule(config, "Complex conditions should be simplified and extracted into well-named methods if necessary.") {

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
        val logicalOperators = condition?.collectDescendantsOfType<KtBinaryExpression> {
            it.operationToken == KtTokens.ANDAND || it.operationToken == KtTokens.OROR
        } ?: return

        if (logicalOperators.isEmpty()) return

        val count = logicalOperators.size + 1
        if (count > allowedConditions) {
            report(
                Finding(
                    Entity.from(condition),
                    "This condition is too complex ($count). " +
                        "The defined maximum number of allowed conditions is set to '$allowedConditions'"
                )
            )
        }
    }
}
