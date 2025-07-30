package dev.detekt.rules.performance

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
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
 * See more details here:
 * [Exploring Kotlin Hidden Costs - Part 1](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-1-fbb9935d9b62)
 * [Exploring Kotlin Hidden Costs - Part 2](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-2-324a4a50b70)
 * [Exploring Kotlin Hidden Costs - Part 3](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-3-3bf6e0dbf0a4)
 *
 * To solve this code smell, the forEach usage should be replaced by a for loop.
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
                report(Finding(Entity.from(forExpression), description))
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
