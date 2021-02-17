package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable

/**
 * Disallow to cast to nullable types. There are cases where `as String?` is misused as the safe cast (`as? String`),
 * so if you want to prevent those cases, turn on this rule.
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
 *
 * @requiresTypeResolution
 */
class CastToNullableType(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Disallow to cast to nullable types",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        super.visitBinaryWithTypeRHSExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val operationReference = expression.operationReference
        if (operationReference.getReferencedNameElementType() != KtTokens.AS_KEYWORD) return

        val typeReference = expression.right ?: return
        val type = bindingContext[BindingContext.TYPE, expression.right] ?: return
        if (!type.isMarkedNullable) return

        val message = "Use the safe cast ('as? ${type.makeNotNullable()}') instead of 'as ${typeReference.text}'."
        report(CodeSmell(issue, Entity.from(expression), message))
    }
}
