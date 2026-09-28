package dev.detekt.rules.standardlibrary

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isCalling
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.singleCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelector
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis

/**
 * Long chains of collection operations will have a performance penalty due to a new list being created for each call. Consider using sequences instead. Read more about this in the [documentation](https://kotlinlang.org/docs/sequences.html)
 *
 * <noncompliant>
 * listOf(1, 2, 3, 4).map { it*2 }.filter { it < 4 }.map { it*it }
 *
 * "a, b, c".split(",").dropWhile { it.isEmpty() }.drop(1).takeWhile { it.isNotBlank() }
 * </noncompliant>
 *
 * <compliant>
 * listOf(1, 2, 3, 4).asSequence().map { it*2 }.filter { it < 4 }.map { it*it }.toList()
 *
 * listOf(1, 2, 3, 4).map { it*2 }
 *
 * "a, b, c".splitToSequence(",").dropWhile { it.isEmpty() }.drop(1).takeWhile { it.isNotBlank() }
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

        if (!expression.isCallingCollectionFunPresentInSequenceReturningSequence()) return

        var counter = 1
        var nextCall = expression.nextChainedCall()
        while (nextCall != null) {
            visitedCallExpressions += nextCall
            if (!nextCall.isCallingCollectionFunPresentInSequenceReturningSequence()) {
                break
            }

            counter++
            nextCall = nextCall.nextChainedCall()
        }

        if (counter > allowedOperations) {
            val splitCall = expression.previousChainedCall()?.takeIf { it.isStringSplitCall() }
            if (splitCall != null) {
                val message = "${splitCall.text} could be ${splitCall.text.replaceFirst(SPLIT, SPLIT_TO_SEQUENCE)}"
                report(Finding(Entity.from(splitCall), message))
            } else {
                val message = "${expression.text} could be .asSequence().${expression.text}"
                report(Finding(Entity.from(expression), message))
            }
        }
    }

    private fun KtExpression.isCallingCollectionFunPresentInSequenceReturningSequence(): Boolean {
        ((this as? KtCallExpression)?.getCallNameExpression()?.getReferencedName())?.let {
            if (it in listOfAllowedFunFromCollections) return false
        }
        return analyze(this) {
            val callableId = resolveToCall()
                ?.singleCallOrNull<KaCallableMemberCall<*, *>>()
                ?.symbol
                ?.callableId
            callableId?.packageName == StandardClassIds.BASE_COLLECTIONS_PACKAGE &&
                findTopLevelCallables(StandardClassIds.BASE_SEQUENCES_PACKAGE, callableId.callableName)
                    .any { it.returnType.symbol?.classId?.asFqNameString() == SEQUENCE_CLASS_STR }
        }
    }

    private fun KtCallExpression.isStringSplitCall(): Boolean = isCalling(splitCallableId)

    private fun KtExpression.nextChainedCall(): KtExpression? {
        val expression = this.getQualifiedExpressionForSelectorOrThis()
        return expression.getQualifiedExpressionForReceiver()?.selectorExpression
    }

    private fun KtExpression.previousChainedCall(): KtCallExpression? {
        val qualified = getQualifiedExpressionForSelector() ?: return null
        return when (val receiver = qualified.receiverExpression) {
            is KtCallExpression -> receiver
            is KtQualifiedExpression -> receiver.selectorExpression as? KtCallExpression
            else -> null
        }
    }

    companion object {
        private const val SEQUENCE_CLASS_STR = "kotlin.sequences.Sequence"
        private const val SPLIT = "split"
        private const val SPLIT_TO_SEQUENCE = "splitToSequence"
        private val splitCallableId = CallableId(StandardClassIds.BASE_TEXT_PACKAGE, Name.identifier(SPLIT))
        private val listOfAllowedFunFromCollections = listOf("asSequence")
    }
}
