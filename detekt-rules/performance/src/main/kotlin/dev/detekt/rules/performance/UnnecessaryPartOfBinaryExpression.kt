package dev.detekt.rules.performance

import com.intellij.psi.tree.IElementType
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.utils.addIfNotNull

/**
 * This rule applies to unnecessary binary expressions, including those in `if` and `when` conditions, as well as all predicates.
 * Binary expressions with `||` and `&&` operator are checked.
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
class UnnecessaryPartOfBinaryExpression(config: Config) :
    Rule(config, "Detects unnecessary checks in binary expressions.") {

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        val operator = expression.operationToken
        if (operator != KtTokens.OROR && operator != KtTokens.ANDAND) return

        val parent = expression.parent
        if (parent is KtBinaryExpression && parent.operationToken == operator) return

        val expressions = expression.expressions(operator).map { it.text.replace(whiteSpace, "") }
        if (expressions.size != expressions.distinct().size) {
            report(Finding(Entity.from(expression), description))
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
