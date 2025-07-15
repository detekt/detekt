package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi2ir.deparenthesize

/**
 * This rule detects `?: emptyList()` that can be replaced with `orEmpty()` call.
 *
 * <noncompliant>
 * fun test(x: List<Int>?, s: String?) {
 *     val a = x ?: emptyList()
 *     val b = s ?: ""
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun test(x: List<Int>?, s: String?) {
 *     val a = x.orEmpty()
 *     val b = s.orEmpty()
 * }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.21.0")
class UseOrEmpty(config: Config) :
    Rule(
        config,
        "Use `orEmpty()` call instead of `?:` with empty collection factory methods",
    ),
    RequiresAnalysisApi {

    @Suppress("ReturnCount")
    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (expression.operationToken != KtTokens.ELVIS) return
        val left = expression.left ?: return
        val right = expression.right ?: return
        if (!right.isEmptyElement()) return

        val leftType = analyze(left) {
            val leftType = left.expressionType ?: return
            if (!leftType.nullability.isNullable) return
            left.deparenthesize().let {
                if (it is KtArrayAccessExpression) {
                    val called = it.resolveToCall()?.singleFunctionCallOrNull()?.symbol as? KaNamedFunctionSymbol
                    if (called?.isOperator == true && called.typeParameters.isNotEmpty()) return
                }
            }
            leftType
        }

        analyze(right) {
            val rightClassId = right.expressionType?.symbol?.classId ?: return
            if (!leftType.isSubtypeOf(rightClassId)) return
        }

        val message = "This '${KtTokens.ELVIS.value} ${right.text}' can be replaced with 'orEmpty()' call"
        report(Finding(Entity.from(expression), message))
    }

    private fun KtExpression.isEmptyElement(): Boolean {
        when (this) {
            is KtCallExpression -> {
                val calleeText = calleeExpression?.text ?: return false
                val emptyFunction = emptyFunctions[calleeText]
                val factoryFunction = factoryFunctions[calleeText]
                if (emptyFunction == null && factoryFunction == null) return false
                analyze(this) {
                    val callableId = resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId ?: return false
                    return callableId == emptyFunction || callableId == factoryFunction && valueArguments.isEmpty()
                }
            }
            is KtStringTemplateExpression -> return entries.isEmpty()
            else -> return false
        }
    }

    companion object {
        private val emptyFunctions = listOf(
            StandardClassIds.BASE_COLLECTIONS_PACKAGE to "emptyList",
            StandardClassIds.BASE_COLLECTIONS_PACKAGE to "emptySet",
            StandardClassIds.BASE_COLLECTIONS_PACKAGE to "emptyMap",
            StandardClassIds.BASE_SEQUENCES_PACKAGE to "emptySequence",
            StandardClassIds.BASE_KOTLIN_PACKAGE to "emptyArray",
        ).associate { (pkg, functionName) ->
            functionName to CallableId(pkg, Name.identifier(functionName))
        }

        private val factoryFunctions = listOf(
            StandardClassIds.BASE_COLLECTIONS_PACKAGE to "listOf",
            StandardClassIds.BASE_COLLECTIONS_PACKAGE to "setOf",
            StandardClassIds.BASE_COLLECTIONS_PACKAGE to "mapOf",
            StandardClassIds.BASE_SEQUENCES_PACKAGE to "sequenceOf",
            StandardClassIds.BASE_KOTLIN_PACKAGE to "arrayOf",
        ).associate { (pkg, functionName) ->
            functionName to CallableId(pkg, Name.identifier(functionName))
        }
    }
}
