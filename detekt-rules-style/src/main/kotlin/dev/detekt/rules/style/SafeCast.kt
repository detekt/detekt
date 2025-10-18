package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

/**
 * This rule inspects casts and reports casts which could be replaced with safe casts instead.
 *
 * <noncompliant>
 * fun numberMagic(number: Number) {
 *     val i = if (number is Int) number else null
 *     // ...
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun numberMagic(number: Number) {
 *     val i = number as? Int
 *     // ...
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.0.0")
class SafeCast(config: Config) : Rule(
    config,
    "Prefer to use a safe cast instead of if-else-null."
) {

    override fun visitIfExpression(expression: KtIfExpression) {
        val condition = expression.condition
        if (condition is KtIsExpression) {
            val leftHandSide = condition.leftHandSide
            if (leftHandSide is KtNameReferenceExpression) {
                val identifier = leftHandSide.text
                val thenClause = expression.then
                val elseClause = expression.`else`
                val result = when (condition.isNegated) {
                    true -> isIfElseNull(elseClause, thenClause, identifier)
                    false -> isIfElseNull(thenClause, elseClause, identifier)
                }
                if (result) {
                    addReport(expression)
                }
            }
        }
    }

    private fun isIfElseNull(thenClause: KtExpression?, elseClause: KtExpression?, identifier: String): Boolean =
        thenClause.isIdentifier(identifier) && elseClause.isNullConstant()

    private fun KtExpression?.isIdentifier(identifier: String): Boolean = singleExpression()?.text == identifier

    private fun KtExpression?.isNullConstant(): Boolean {
        val singleExpression = singleExpression() ?: return false
        return singleExpression is KtConstantExpression && singleExpression.node.elementType == KtNodeTypes.NULL
    }

    private fun KtExpression?.singleExpression(): KtExpression? =
        if (this is KtBlockExpression) children.singleOrNull() as? KtExpression else this

    private fun addReport(expression: KtIfExpression) {
        report(Finding(Entity.from(expression), "This cast should be replaced with a safe cast: as?"))
    }
}
