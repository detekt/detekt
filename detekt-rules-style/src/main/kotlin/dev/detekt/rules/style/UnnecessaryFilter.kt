package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis
import org.jetbrains.kotlin.psi.psiUtil.siblings
import org.jetbrains.kotlin.psi.unpackFunctionLiteral
import org.jetbrains.kotlin.resolve.calls.util.getCalleeExpressionIfAny

/**
 * Unnecessary filters add complexity to the code and accomplish nothing. They should be removed.
 *
 * <noncompliant>
 * val x = listOf(1, 2, 3)
 *      .filter { it > 1 }
 *      .count()
 *
 * val x = listOf(1, 2, 3)
 *      .filter { it > 1 }
 *      .isEmpty()
 * </noncompliant>
 *
 * <compliant>
 * val x = listOf(1, 2, 3)
 *      .count { it > 2 }
 *
 * val x = listOf(1, 2, 3)
 *      .none { it > 1 }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.21.0")
class UnnecessaryFilter(config: Config) :
    Rule(
        config,
        "`filter()` with other collection operations may be simplified."
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.matchingCall(filterFqNames) == null) return
        val lambdaArgumentText = expression.lambda()?.text ?: return

        val qualifiedOrCall = expression.getQualifiedExpressionForSelectorOrThis()
        val nextCall = qualifiedOrCall.nextCall() ?: return
        if (nextCall is KtCallExpression && nextCall.valueArguments.isNotEmpty()) return

        nextCall.matchingCall(secondCalls.keys)
            ?.let { secondCalls[it] }
            ?.let {
                val message = "'${expression.text}' can be replaced by '${it.correctOperator} $lambdaArgumentText'"
                report(Finding(Entity.from(expression), message))
            }
    }

    private fun KtCallExpression.lambda(): KtLambdaExpression? {
        val argument = lambdaArguments.singleOrNull() ?: valueArguments.singleOrNull()
        return argument?.getArgumentExpression()?.unpackFunctionLiteral()
    }

    private fun KtExpression.matchingCall(fqNames: Set<FqName>): FqName? {
        val calleeText = getCalleeExpressionIfAny()?.text ?: return null
        if (fqNames.none { it.shortName().asString() == calleeText }) return null
        return analyze(this) {
            val callableId = resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId
                ?: (mainReference?.resolveToSymbol() as? KaCallableSymbol)?.callableId
            callableId?.asSingleFqName()?.takeIf { it in fqNames }
        }
    }

    @Suppress("ReturnCount")
    private fun KtExpression.nextCall(): KtExpression? {
        getQualifiedExpressionForReceiver()?.selectorExpression?.let { return it }

        val property = parentProperty()
        if (property != null) {
            analyze(property) {
                val propertySymbol = property.symbol
                val propertyName = propertySymbol.name.asString()
                val singleReferrer = property.siblings(forward = true, withItself = false).flatMap { sibling ->
                    sibling.collectDescendantsOfType<KtNameReferenceExpression> {
                        it.text == propertyName && it.mainReference.resolveToSymbol() == propertySymbol
                    }
                }.singleOrNull()
                val qualified = singleReferrer?.getQualifiedExpressionForReceiver()
                if (qualified?.parentProperty() != null || qualified?.parentReturn() != null) {
                    return qualified.selectorExpression
                }
            }
        }
        return null
    }

    private fun KtExpression.parentProperty(): KtProperty? =
        (parent as? KtProperty)?.takeIf { it.initializer == this }

    private fun KtExpression.parentReturn(): KtReturnExpression? =
        (parent as? KtReturnExpression)?.takeIf { it.returnedExpression == this }

    private data class SecondCall(val fqName: FqName, val correctOperator: String = fqName.shortName().asString())

    companion object {
        private val filterFqNames = setOf(
            FqName("kotlin.collections.filter"),
            FqName("kotlin.sequences.filter"),
            FqName("kotlin.text.filter"),
        )

        private val secondCalls = listOf(
            SecondCall(FqName("kotlin.collections.List.size"), "count"),
            SecondCall(FqName("kotlin.collections.List.isEmpty"), "none"),
            SecondCall(FqName("kotlin.collections.isNotEmpty"), "any"),
            SecondCall(FqName("kotlin.collections.count")),
            SecondCall(FqName("kotlin.collections.any")),
            SecondCall(FqName("kotlin.collections.none")),
            SecondCall(FqName("kotlin.collections.first")),
            SecondCall(FqName("kotlin.collections.firstOrNull")),
            SecondCall(FqName("kotlin.collections.last")),
            SecondCall(FqName("kotlin.collections.lastOrNull")),
            SecondCall(FqName("kotlin.collections.single")),
            SecondCall(FqName("kotlin.collections.singleOrNull")),
            SecondCall(FqName("kotlin.sequences.count")),
            SecondCall(FqName("kotlin.sequences.any")),
            SecondCall(FqName("kotlin.sequences.none")),
            SecondCall(FqName("kotlin.sequences.first")),
            SecondCall(FqName("kotlin.sequences.firstOrNull")),
            SecondCall(FqName("kotlin.sequences.last")),
            SecondCall(FqName("kotlin.sequences.lastOrNull")),
            SecondCall(FqName("kotlin.sequences.single")),
            SecondCall(FqName("kotlin.sequences.singleOrNull")),
            SecondCall(FqName("kotlin.text.isEmpty"), "none"),
            SecondCall(FqName("kotlin.text.isNotEmpty"), "any"),
            SecondCall(FqName("kotlin.text.count")),
            SecondCall(FqName("kotlin.text.any")),
            SecondCall(FqName("kotlin.text.none")),
            SecondCall(FqName("kotlin.text.first")),
            SecondCall(FqName("kotlin.text.firstOrNull")),
            SecondCall(FqName("kotlin.text.last")),
            SecondCall(FqName("kotlin.text.lastOrNull")),
            SecondCall(FqName("kotlin.text.single")),
            SecondCall(FqName("kotlin.text.singleOrNull")),
        ).associateBy { it.fqName }
    }
}
