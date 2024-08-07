package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * Reports unsafe cast to nullable types.
 * `as String?` is unsafed and may be misused as safe cast (`as? String`).
 *
 * <noncompliant>
 * fun foo(a: Any?) {
 *     val x: String? = a as String? // If 'a' is not String, ClassCastException will be thrown.
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(a: Any?) {
 *     val x: String? = a as? String
 * }
 * </compliant>
 */

class CastToNullableType(config: Config) :
    Rule(
        config,
        "Use safe cast instead of unsafe cast to nullable types."
    ),
    RequiresTypeResolution {
    @Suppress("ReturnCount")
    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        super.visitBinaryWithTypeRHSExpression(expression)

        val operationReference = expression.operationReference
        if (operationReference.getReferencedNameElementType() != KtTokens.AS_KEYWORD) return
        if (expression.left.text == KtTokens.NULL_KEYWORD.value) return
        val nullableTypeElement = expression.right?.typeElement as? KtNullableType ?: return
        val expressionType =
            bindingContext[BindingContext.EXPRESSION_TYPE_INFO, expression.left]?.type ?: return
        val castedType = bindingContext[BindingContext.TYPE, expression.right] ?: return

        if (expressionType == castedType || expressionType.supertypes().contains(castedType)) return

        val message = "Use the safe cast ('as? ${nullableTypeElement.innerType?.text}')" +
            " instead of 'as ${nullableTypeElement.text}'."
        report(CodeSmell(Entity.from(operationReference), message))
    }
}
