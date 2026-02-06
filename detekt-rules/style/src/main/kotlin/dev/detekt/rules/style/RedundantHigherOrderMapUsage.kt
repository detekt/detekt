package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.unpackFunctionLiteral

/**
 * Redundant maps add complexity to the code and accomplish nothing. They should be removed or replaced with the proper
 * operator.
 *
 * <noncompliant>
 * fun foo(list: List<Int>): List<Int> {
 *     return list
 *         .filter { it > 5 }
 *         .map { it }
 * }
 *
 * fun bar(list: List<Int>): List<Int> {
 *     return list
 *         .filter { it > 5 }
 *         .map {
 *             doSomething(it)
 *             it
 *         }
 * }
 *
 * fun baz(set: Set<Int>): List<Int> {
 *     return set.map { it }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(list: List<Int>): List<Int> {
 *     return list
 *         .filter { it > 5 }
 * }
 *
 * fun bar(list: List<Int>): List<Int> {
 *     return list
 *         .filter { it > 5 }
 *         .onEach {
 *             doSomething(it)
 *         }
 * }
 *
 * fun baz(set: Set<Int>): List<Int> {
 *     return set.toList()
 * }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.21.0")
class RedundantHigherOrderMapUsage(config: Config) :
    Rule(
        config,
        "Checks for redundant 'map' calls, which can be removed."
    ),
    RequiresAnalysisApi {

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val calleeExpression = expression.calleeExpression
        if (calleeExpression?.text != "map") return
        val functionLiteral = expression.lambda()?.functionLiteral
        val lambdaStatements = functionLiteral?.bodyExpression?.statements ?: return

        analyze(functionLiteral) {
            val partiallyAppliedSymbol =
                expression.resolveToCall()?.singleFunctionCallOrNull()?.partiallyAppliedSymbol ?: return
            val symbol = partiallyAppliedSymbol.symbol
            if (symbol.callableId !in mapCallableIds) return

            val receiver = partiallyAppliedSymbol.dispatchReceiver ?: partiallyAppliedSymbol.extensionReceiver
            val receiverType = receiver?.type ?: return
            val receiverIsList = receiverType.isSubtypeOf(listClassId)
            val receiverIsSet = receiverType.isSubtypeOf(setClassId)
            val receiverIsSequence = receiverType.isSubtypeOf(sequenceClassId)
            if (!receiverIsList && !receiverIsSet && !receiverIsSequence) return

            if (!isRedundant(functionLiteral, lambdaStatements)) return

            val message = when {
                lambdaStatements.size != 1 -> "This 'map' call can be replaced with 'onEach' or 'forEach'."
                receiverIsSet -> "This 'map' call can be replaced with 'toList'."
                else -> "This 'map' call can be removed."
            }
            report(Finding(Entity.from(calleeExpression), message))
        }
    }

    private fun KtCallExpression.lambda(): KtLambdaExpression? {
        val argument = lambdaArguments.singleOrNull() ?: valueArguments.singleOrNull() ?: return null
        val lambda = argument.getArgumentExpression()?.unpackFunctionLiteral() ?: return null
        if (lambda.valueParameters.firstOrNull()?.destructuringDeclaration != null) return null
        return lambda
    }

    @Suppress("ReturnCount")
    private fun KaSession.isRedundant(
        functionLiteral: KtFunctionLiteral,
        lambdaStatements: List<KtExpression>,
    ): Boolean {
        val symbol = functionLiteral.symbol
        val lambdaParameter = symbol.valueParameters.singleOrNull() ?: return false
        val lastStatement = lambdaStatements.lastOrNull() ?: return false
        if (!isReferenceTo(lastStatement, lambdaParameter)) return false
        val labeledReturnExpressions = functionLiteral.collectDescendantsOfType<KtReturnExpression> {
            if (it == lastStatement) return@collectDescendantsOfType false
            val label = (it as? KtExpressionWithLabel)?.getTargetLabel() ?: return@collectDescendantsOfType false
            label.mainReference.resolveToSymbol() == symbol
        }
        return labeledReturnExpressions.all { isReferenceTo(it, lambdaParameter) }
    }

    private fun KaSession.isReferenceTo(expression: KtExpression, symbol: KaValueParameterSymbol): Boolean {
        val nameReference = when (expression) {
            is KtReturnExpression -> expression.returnedExpression
            else -> expression
        } as? KtNameReferenceExpression ?: return false
        return nameReference.mainReference.resolveToSymbol() == symbol
    }

    companion object {
        private val mapCallableIds = buildList {
            val map = Name.identifier("map")
            add(CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, map))
            add(CallableId(StandardClassIds.BASE_SEQUENCES_PACKAGE, map))
        }
        private val listClassId = StandardClassIds.List
        private val setClassId = StandardClassIds.Set
        private val sequenceClassId = ClassId(StandardClassIds.BASE_SEQUENCES_PACKAGE, Name.identifier("Sequence"))
    }
}
