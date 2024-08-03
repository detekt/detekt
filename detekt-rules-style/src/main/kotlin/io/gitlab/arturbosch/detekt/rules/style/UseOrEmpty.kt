package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi2ir.deparenthesize
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable

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
    RequiresTypeResolution {
    @Suppress("ReturnCount")
    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (expression.operationToken != KtTokens.ELVIS) return
        val left = expression.left ?: return
        val right = expression.right ?: return
        if (!right.isEmptyElement()) return

        val leftType = left.getType(bindingContext) ?: return
        if (!leftType.isNullable()) return
        if (left.deparenthesize() is KtArrayAccessExpression) {
            val functionDescriptor = left.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor
            if (functionDescriptor != null &&
                functionDescriptor.isOperator &&
                functionDescriptor.typeParameters.isNotEmpty()
            ) {
                return
            }
        }

        val rightType = right.getType(bindingContext) ?: return
        if (!leftType.makeNotNullable().isSubtypeOf(rightType)) return

        val message = "This '${KtTokens.ELVIS.value} ${right.text}' can be replaced with 'orEmpty()' call"
        report(CodeSmell(Entity.from(expression), message))
    }

    private fun KtExpression.isEmptyElement(): Boolean {
        when (this) {
            is KtCallExpression -> {
                val calleeText = calleeExpression?.text ?: return false
                val emptyFunction = emptyFunctions[calleeText]
                val factoryFunction = factoryFunctions[calleeText]
                if (emptyFunction == null && factoryFunction == null) return false
                val fqName = getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() ?: return false
                return fqName == emptyFunction || fqName == factoryFunction && valueArguments.isEmpty()
            }
            is KtStringTemplateExpression -> return entries.isEmpty()
            else -> return false
        }
    }

    companion object {
        private val emptyFunctions = listOf(
            "kotlin.collections.emptyList",
            "kotlin.collections.emptySet",
            "kotlin.collections.emptyMap",
            "kotlin.sequences.emptySequence",
            "kotlin.emptyArray",
        ).associate {
            val fqName = FqName(it)
            fqName.shortName().asString() to fqName
        }

        private val factoryFunctions = listOf(
            "kotlin.collections.listOf",
            "kotlin.collections.setOf",
            "kotlin.collections.mapOf",
            "kotlin.sequences.sequenceOf",
            "kotlin.arrayOf",
        ).associate {
            val fqName = FqName(it)
            fqName.shortName().asString() to fqName
        }
    }
}
