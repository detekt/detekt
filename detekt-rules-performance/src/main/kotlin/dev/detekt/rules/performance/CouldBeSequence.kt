package dev.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.singleCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis

/**
 * Long chains of collection operations will have a performance penalty due to a new list being created for each call. Consider using sequences instead. Read more about this in the [documentation](https://kotlinlang.org/docs/sequences.html)
 *
 * <noncompliant>
 * listOf(1, 2, 3, 4).map { it*2 }.filter { it < 4 }.map { it*it }
 * </noncompliant>
 *
 * <compliant>
 * listOf(1, 2, 3, 4).asSequence().map { it*2 }.filter { it < 4 }.map { it*it }.toList()
 *
 * listOf(1, 2, 3, 4).map { it*2 }
 * </compliant>
 */
class CouldBeSequence(config: Config) :
    Rule(
        config,
        "Several chained collection operations that should be a sequence."
    ),
    RequiresAnalysisApi {

    @Configuration("The maximum number of allowed chained collection operations.")
    private val allowedOperations: Int by config(defaultValue = 2)

    private val visitedCallExpressions = mutableListOf<KtExpression>()

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (visitedCallExpressions.contains(expression)) return

        if (!expression.isCallingKotlinCollectionFunPresentInSequence()) return

        var counter = 1
        var nextCall = expression.nextChainedCall()
        while (nextCall != null) {
            visitedCallExpressions += nextCall
            if (!nextCall.isCallingKotlinCollectionFunPresentInSequence()) {
                break
            }

            counter++
            nextCall = nextCall.nextChainedCall()
        }

        if (counter > allowedOperations) {
            val message = "${expression.text} could be .asSequence().${expression.text}"
            report(Finding(Entity.from(expression), message))
        }
    }

    private fun KtExpression.isCallingKotlinCollectionFunPresentInSequence(): Boolean = analyze(this) {
        val callableId = resolveToCall()
            ?.singleCallOrNull<KaCallableMemberCall<*, *>>()
            ?.symbol
            ?.callableId
        callableId?.packageName == StandardClassIds.BASE_COLLECTIONS_PACKAGE &&
            findTopLevelCallables(StandardClassIds.BASE_SEQUENCES_PACKAGE, callableId.callableName).any()
    }

    private fun KtExpression.nextChainedCall(): KtExpression? {
        val expression = this.getQualifiedExpressionForSelectorOrThis()
        return expression.getQualifiedExpressionForReceiver()?.selectorExpression
    }
}
