package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.api.valuesWithReason
import dev.detekt.psi.FunctionMatcher
import dev.detekt.psi.FunctionMatcher.Companion.fromFunctionSignature
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCall
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.KaCompoundAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.KaCompoundArrayAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.KaCompoundVariableAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertyAccessorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.psiUtil.isDotSelector
import org.jetbrains.kotlin.resolve.calls.util.asCallableReferenceExpression
import org.jetbrains.kotlin.resolve.calls.util.getCalleeExpressionIfAny

/**
 * Reports all method or constructor invocations that are forbidden.
 *
 * This rule allows to set a list of forbidden [methods] or constructors. This can be used to discourage the use
 * of unstable, experimental or deprecated methods, especially for methods imported from external libraries.
 *
 * <noncompliant>
 * fun main() {
 *   println()
 *   val myPrintln : () -> Unit = ::println
 *   kotlin.io.print("Hello, World!")
 * }
 * </noncompliant>
 *
 */
class ForbiddenMethodCall(config: Config) :
    Rule(
        config,
        "Mark forbidden methods. A forbidden method could be an invocation of an unstable / experimental " +
            "method and hence you might want to mark it as forbidden in order to get warned about the usage."
    ),
    RequiresAnalysisApi {

    @Configuration(
        "List of fully qualified method signatures which are forbidden. " +
            FunctionMatcher.FUNCTION_MATCHER_DOC
    )
    private val methods: List<ForbiddenMethod> by config(
        valuesWithReason(
            "kotlin.io.print" to "print does not allow you to configure the output stream. Use a logger instead.",
            "kotlin.io.println" to "println does not allow you to configure the output stream. Use a logger instead.",
            "java.math.BigDecimal.<init>(kotlin.Double)" to "using `BigDecimal(Double)` can result in " +
                "unexpected floating point precision behavior. Use `BigDecimal.valueOf(Double)` or " +
                "`String.toBigDecimalOrNull()` instead.",
            "java.math.BigDecimal.<init>(kotlin.String)" to "using `BigDecimal(String)` can result in a " +
                "`NumberFormatException`. Use `String.toBigDecimalOrNull()`",
            "kotlin.system.measureTimeMillis" to "It is marked as obsolete. Use `kotlin.time.measureTime` instead.",
        )
    ) { list ->
        list.map { ForbiddenMethod(fromFunctionSignature(it.value), it.reason) }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        check(expression)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)
        check(expression.operationReference)
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        if (expression.getCalleeExpressionIfAny()?.isDotSelector() == true) {
            check(expression)
        }
    }

    override fun visitPrefixExpression(expression: KtPrefixExpression) {
        super.visitPrefixExpression(expression)
        check(expression.operationReference)
    }

    override fun visitPostfixExpression(expression: KtPostfixExpression) {
        super.visitPostfixExpression(expression)
        check(expression.operationReference)
    }

    override fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression) {
        super.visitCallableReferenceExpression(expression)
        check(expression.callableReference)
    }

    private fun check(expression: KtExpression) {
        analyze(expression) {
            val call = expression.resolveToCall()
                ?: expression.asCallableReferenceExpression()?.resolveToCall()
                ?: return

            val successfulCall = call.successfulCallOrNull<KaCall>() ?: return

            getCallInfos(successfulCall, expression).forEach { (expressionPropertySymbol, symbol) ->
                symbol ?: return@forEach
                val forbiddenMethod = methods.find { method ->
                    method.value.match(expressionPropertySymbol, symbol)
                }
                if (forbiddenMethod != null) {
                    val message = if (forbiddenMethod.reason != null) {
                        "The method `${forbiddenMethod.value}` has been forbidden: ${forbiddenMethod.reason}"
                    } else {
                        "The method `${forbiddenMethod.value}` has been forbidden in the detekt config."
                    }
                    report(Finding(Entity.from(expression), message))
                }
            }
        }
    }

    private fun KaSession.getCallInfos(
        kaCall: KaCall,
        expression: KtExpression,
    ): Sequence<Pair<KaPropertySymbol?, KaCallableSymbol?>> =
        sequence {
            val symbols = when (kaCall) {
                is KaCallableMemberCall<*, *> -> {
                    val expressionSymbol = kaCall.partiallyAppliedSymbol.symbol
                    sequenceOf(expressionSymbol).plus(expressionSymbol.allOverriddenSymbols).map {
                        if (
                            it is KaPropertySymbol &&
                            it.isGetterOrSetter() &&
                            expression !is KtOperationReferenceExpression
                        ) {
                            it to getPropertyAccessorSymbol(it, expression)
                        } else {
                            null to it
                        }
                    }
                }

                is KaCompoundAccessCall -> sequenceOf(
                    null to kaCall.compoundOperation.operationPartiallyAppliedSymbol.symbol
                )

                is KaCompoundArrayAccessCall -> null

                is KaCompoundVariableAccessCall -> null
            } ?: return@sequence

            yieldAll(symbols)
        }

    private fun KaPropertySymbol.isGetterOrSetter(): Boolean = hasSetter || hasGetter

    private fun getPropertyAccessorSymbol(
        appliedSymbol: KaPropertySymbol,
        expression: KtExpression,
    ): KaPropertyAccessorSymbol? {
        val parent = expression.parent
        return when {
            parent is KtBinaryExpression && parent.operationToken == KtTokens.EQ -> appliedSymbol.setter
            else -> appliedSymbol.getter
        }
    }

    internal data class ForbiddenMethod(val value: FunctionMatcher, val reason: String?)
}
