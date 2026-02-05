package dev.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS

/**
 * Reports cast of unnecessary type casting. Cases like this can be
 * replaced with type checking for performance reasons.
 *
 * <noncompliant>
 * fun foo() {
 *     val objList: List<Any> = emptyList()
 *     objList.any { it as? String != null }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     val objList: List<Any> = emptyList()
 *     objList.any { it is String }
 * }
 * </compliant>
 */
class UnnecessaryTypeCasting(config: Config) :
    Rule(
        config,
        "Unnecessary type casting is found. Consider using type checking."
    ) {

    @Suppress("ReturnCount")
    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        super.visitBinaryWithTypeRHSExpression(expression)

        val operationReference = expression.operationReference
        if (operationReference.getReferencedNameElementType() != KtTokens.AS_SAFE) return

        val parent = expression.parent as? KtBinaryExpression ?: return
        if (parent.operationReference.getReferencedNameElementType() != KtTokens.EXCLEQ) return

        if (parent.right?.text != KtTokens.NULL_KEYWORD.value &&
            parent.left?.text != KtTokens.NULL_KEYWORD.value
        ) {
            return
        }

        val message =
            "Unnecessary type casting, which can cause perf issues. Use type checking instead."
        report(Finding(Entity.from(operationReference), message))
    }
}
