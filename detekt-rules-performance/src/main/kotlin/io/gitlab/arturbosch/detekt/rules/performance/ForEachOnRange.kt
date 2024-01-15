package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression
import org.jetbrains.kotlin.psi2ir.deparenthesize

/**
 * Using the forEach method on ranges has a heavy performance cost. Prefer using simple for loops.
 *
 * Benchmarks have shown that using forEach on a range can have a huge performance cost in comparison to
 * simple for loops. Hence, in most contexts, a simple for loop should be used instead.
 * See more details here: https://sites.google.com/a/athaydes.com/renato-athaydes/posts/kotlinshiddencosts-benchmarks
 * To solve this CodeSmell, the forEach usage should be replaced by a for loop.
 *
 * <noncompliant>
 * (1..10).forEach {
 *     println(it)
 * }
 * (1 until 10).forEach {
 *     println(it)
 * }
 * (10 downTo 1).forEach {
 *     println(it)
 * }
 * </noncompliant>
 *
 * <compliant>
 * for (i in 1..10) {
 *     println(i)
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.0.0")
class ForEachOnRange(config: Config) : Rule(
    config,
    "Using the forEach method on ranges has a heavy performance cost. Prefer using simple for loops."
) {

    private val rangeOperators = setOf("..", "rangeTo", "downTo", "until", "..<")

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        expression.getCallNameExpression()?.let {
            if (!it.textMatches("forEach")) {
                return
            }
            val forExpression = it.getReceiverExpression()?.deparenthesize()
            if (forExpression != null && isRangeOperatorsChainCall(forExpression)) {
                report(CodeSmell(Entity.from(forExpression), description))
            }
        }
    }

    private fun isRangeOperatorsChainCall(expression: KtElement): Boolean {
        return if (isRangeOperator(expression)) {
            true
        } else {
            when (expression) {
                is KtBinaryExpression -> {
                    val receiverExpression = expression.left?.deparenthesize() ?: return false
                    isRangeOperatorsChainCall(receiverExpression)
                }

                is KtDotQualifiedExpression -> {
                    val receiverExpression = expression.receiverExpression.deparenthesize()
                    isRangeOperatorsChainCall(receiverExpression)
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun isRangeOperator(expression: KtElement): Boolean {
        val operator = when (expression) {
            is KtBinaryExpression -> {
                expression.operationReference.text
            }

            is KtDotQualifiedExpression -> {
                (expression.selectorExpression as? KtCallExpression)?.calleeExpression?.text
            }

            else -> {
                null
            }
        }

        return operator in rangeOperators
    }
}
