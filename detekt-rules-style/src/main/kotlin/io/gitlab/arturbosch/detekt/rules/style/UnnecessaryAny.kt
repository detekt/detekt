package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.firstParameterOrNull
import dev.detekt.psi.isCalling
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.singleCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaDeclarationSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaDestructuringDeclarationSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * Turn on this rule to flag usage of `any` which can either be replaced with simple `contains` call
 * or can removed entirely to reduce visual complexity.
 *
 * <noncompliant>
 * val a = 1
 * list.any { it == a }
 * </noncompliant>
 *
 * <compliant>
 * val a = 1
 * list.contains(a)
 * </compliant>
 */
class UnnecessaryAny(config: Config) :
    Rule(
        config,
        "The `any {  }` usage is unnecessary."
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isCalling(anyCallableId)) return

        val msg = analyze(expression) {
            shouldBeReported(expression)
        }
        if (msg != null) {
            report(
                Finding(
                    Entity.from(expression),
                    msg
                )
            )
        }
    }

    @Suppress("ReturnCount")
    private fun KaSession.shouldBeReported(expression: KtCallExpression): String? {
        val valueArgument = expression.valueArguments.singleOrNull() ?: return null
        return when (val valueExpression = valueArgument.getArgumentExpression()) {
            is KtLambdaExpression -> {
                val bodyExpression = valueExpression.bodyExpression ?: return null
                val parameter =
                    valueExpression.functionLiteral.valueParameters.singleOrNull()?.destructuringDeclaration?.symbol
                        ?: valueExpression.firstParameterOrNull()
                        ?: return null
                bodyExpression.shouldBlockExpressionBeReported(parameter)
            }

            is KtNamedFunction -> {
                val bodyExpression = valueExpression.bodyExpression ?: return null
                val parameter = valueExpression.valueParameters.singleOrNull()?.symbol ?: return null
                if (bodyExpression is KtBlockExpression) {
                    bodyExpression.shouldBlockExpressionBeReported(parameter)
                } else {
                    bodyExpression.shouldStatementBeReported(parameter)
                }
            }

            else -> {
                null
            }
        }
    }

    context(session: KaSession)
    private fun KtBlockExpression.shouldBlockExpressionBeReported(parameter: KaDeclarationSymbol): String? {
        if (this.statements.isEmpty()) return null
        if (parameter is KaDestructuringDeclarationSymbol) {
            return if (parameter.entries.all { getItUsageCount(it) == 0 }) {
                ANY_CAN_BE_OMITTED_MSG
            } else {
                null
            }
        }
        if (this.statements.isNotEmpty() && getItUsageCount(parameter) == 0) return ANY_CAN_BE_OMITTED_MSG

        val firstStatement = this.statements[0]
        val statement = if (firstStatement is KtReturnExpression) {
            firstStatement.returnedExpression
        } else {
            firstStatement
        } ?: return null

        return statement.shouldStatementBeReported(parameter)
    }

    context(session: KaSession)
    private fun KtExpression.shouldStatementBeReported(parameter: KaDeclarationSymbol): String? =
        when (this) {
            is KtBinaryExpression if operationToken == KtTokens.EQEQ -> {
                isUsageOfValueAndItEligible(parameter, left, right)
            }

            is KtDotQualifiedExpression if selectorExpression.isCallingEquals() -> {
                isUsageOfValueAndItEligible(
                    parameter,
                    receiverExpression,
                    (selectorExpression as? KtCallExpression)?.valueArguments?.firstOrNull()?.getArgumentExpression(),
                )
            }

            else -> {
                if (this.getItUsageCount(parameter) <= 0) ANY_CAN_BE_OMITTED_MSG else null
            }
        }

    @Suppress("ReturnCount")
    context(session: KaSession)
    private fun isUsageOfValueAndItEligible(
        parameter: KaDeclarationSymbol,
        leftExpression: KtExpression?,
        rightExpression: KtExpression?,
    ): String? {
        leftExpression ?: return null
        rightExpression ?: return null

        val itRefCountInLeft = leftExpression.getItUsageCount(parameter)
        val itRefCountInRight = rightExpression.getItUsageCount(parameter)
        return when {
            itRefCountInLeft > 0 && itRefCountInRight > 0 -> {
                // both side `it` has been used
                null
            }

            itRefCountInLeft == 0 && itRefCountInRight == 0 -> {
                // no side has `it`
                ANY_CAN_BE_OMITTED_MSG
            }

            itRefCountInRight == 1 -> {
                // reversing the order of parameter
                isUsageOfValueAndItEligible(parameter, rightExpression, leftExpression)
            }

            itRefCountInLeft == 1 -> {
                with(session) {
                    val itExpressionType = (leftExpression.mainReference?.resolveToSymbol() as? KaVariableSymbol)
                        ?.returnType
                        ?: return null
                    val valueExpressionType = rightExpression
                        .resolveToCall()
                        ?.singleCallOrNull<KaCallableMemberCall<*, *>>()
                        ?.symbol
                        ?.returnType
                        ?: return null
                    if (valueExpressionType.isSubtypeOf(itExpressionType)) {
                        USE_CONTAINS_MSG
                    } else {
                        null
                    }
                }
            }

            else -> {
                null
            }
        }
    }

    context(session: KaSession)
    private fun KtExpression.getItUsageCount(symbol: KaDeclarationSymbol) = with(session) {
        collectDescendantsOfType<KtNameReferenceExpression>().count {
            it.mainReference.resolveToSymbol() == symbol
        }
    }

    context(session: KaSession)
    private fun KtExpression?.isCallingEquals(): Boolean {
        if (this == null) return false
        with(session) {
            val symbol = resolveToCall()?.singleFunctionCallOrNull()?.symbol as? KaNamedFunctionSymbol ?: return false
            return symbol.name == StandardNames.EQUALS_NAME &&
                symbol.returnType.isBooleanType &&
                symbol.valueParameters.singleOrNull()?.returnType?.let { it.isAnyType && it.isMarkedNullable } == true
        }
    }

    companion object {
        private val anyCallableId = CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("any"))
        private const val USE_CONTAINS_MSG =
            "Use `contains` instead of `any {  }` call to check the presence of the element"
        private const val ANY_CAN_BE_OMITTED_MSG = "`any {  }` expression can be omitted"
    }
}
