package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.utils.addIfNotNull

/**
 * Unnecessary binary expression add complexity to the code and accomplish nothing. They should be removed.
 * The rule works with all binary expression included if and when condition. The rule also works with all predicates.
 * The rule verify binary expression only in case when the expression use only one type of the following
 * operators || or &&.
 *
 * <noncompliant>
 * val foo = true
 * val bar = true
 *
 * if (foo || bar || foo) {
 * }
 * </noncompliant>
 *
 * <compliant>
 * val foo = true
 * if (foo) {
 * }
 * </compliant>
 *
 */
class UnnecessaryPartOfBinaryExpression(config: Config) : Rule(
    config,
    "Detects duplicate condition into binary expression and recommends to remove unnecessary checks"
) {

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        val operator = expression.operationToken
        if (operator != KtTokens.OROR && operator != KtTokens.ANDAND) return

        val parent = expression.parent
        if (parent is KtBinaryExpression && parent.operationToken == operator) return

        val expressions = expression.expressions(operator).map { it.text.replace(whiteSpace, "") }
        if (expressions.size != expressions.distinct().size) {
            report(CodeSmell(Entity.from(expression), description))
        }
    }

    private fun KtBinaryExpression.expressions(operator: IElementType): List<KtExpression> {
        val expressions = mutableListOf<KtExpression>()
        fun collect(expression: KtExpression?) {
            if (expression is KtBinaryExpression && expression.operationToken == operator) {
                collect(expression.left)
                collect(expression.right)
            } else {
                expressions.addIfNotNull(expression)
            }
        }
        collect(this)
        return expressions
    }

    companion object {
        private val whiteSpace = "\\s".toRegex()
    }
}
