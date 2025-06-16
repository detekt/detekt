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
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.module

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

        if (!expression.isCallingKotlinCollectionFun()) return

        var counter = 1
        var nextCall = expression.nextChainedCall()
        while (nextCall != null) {
            visitedCallExpressions += nextCall
            if (!nextCall.isCallingKotlinCollectionFun()) {
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

    @Suppress("ReturnCount")
    private fun KtExpression.isCallingKotlinCollectionFun(): Boolean {
        val operatorNameFqName = getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull()
            ?.asString()
            ?: return false
        val isExpressionPresentInKotlinCollections =
            operatorNameFqName.startsWith("kotlin.collections.") == true
        if (!isExpressionPresentInKotlinCollections) return false
        val sequenceOperatorFqName =
            FqName("kotlin.sequences." + operatorNameFqName.substringAfter("kotlin.collections."))

        val moduleDescriptor =
            getResolvedCall(bindingContext)?.resultingDescriptor?.module ?: return false

        val sequencePackage = moduleDescriptor.getPackage(FqName("kotlin.sequences"))

        val functionName = sequenceOperatorFqName.shortName()

        val sequenceFunctions = sequencePackage.memberScope.getContributedFunctions(
            functionName,
            NoLookupLocation.FROM_BACKEND
        )

        return sequenceFunctions.isNotEmpty()
    }

    private fun KtExpression.nextChainedCall(): KtExpression? {
        val expression = this.getQualifiedExpressionForSelectorOrThis()
        return expression.getQualifiedExpressionForReceiver()?.selectorExpression
    }
}
