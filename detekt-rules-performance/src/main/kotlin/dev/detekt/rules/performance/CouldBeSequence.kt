package dev.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isCalling
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
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

        if (!expression.isCallingAnyOf(operationsCallableIds)) return

        var counter = 1
        var nextCall = expression.nextChainedCall()
        while (counter <= allowedOperations && nextCall != null) {
            visitedCallExpressions += nextCall
            if (!nextCall.isCallingAnyOf(operationsCallableIds)) {
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

    private fun KtExpression.nextChainedCall(): KtExpression? {
        val expression = this.getQualifiedExpressionForSelectorOrThis()
        return expression.getQualifiedExpressionForReceiver()?.selectorExpression
    }

    private fun KtExpression.isCallingAnyOf(callableIds: List<CallableId>): Boolean {
        val callExpression = this as? KtCallExpression ?: return false
        val calleeText = callExpression.calleeExpression?.text
        val candidates = callableIds.filter { it.callableName.asString() == calleeText }

        return candidates.isNotEmpty() && callExpression.isCalling(candidates)
    }

    companion object {
        private val operationsCallableIds = listOf(
            CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("filter")),
            CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("filterIndexed")),
            CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("map")),
            CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("mapIndexed")),
            CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("flatMap")),
            CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("flatMapIndexed")),
            CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("reduce")),
            CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("zip"))
        )
    }
}
