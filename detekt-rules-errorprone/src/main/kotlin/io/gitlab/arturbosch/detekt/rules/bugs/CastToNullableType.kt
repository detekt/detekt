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
import org.jetbrains.kotlin.psi.KtNullableType

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
class CastToNullableType(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Use safe cast instead of unsafe cast to nullable types.",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        super.visitBinaryWithTypeRHSExpression(expression)

        val operationReference = expression.operationReference
        if (operationReference.getReferencedNameElementType() != KtTokens.AS_KEYWORD) return
        if (expression.left.text == KtTokens.NULL_KEYWORD.value) return
        val nullableTypeElement = expression.right?.typeElement as? KtNullableType ?: return

        val message = "Use the safe cast ('as? ${nullableTypeElement.innerType?.text}')" +
            " instead of 'as ${nullableTypeElement.text}'."
        report(CodeSmell(issue, Entity.from(operationReference), message))
    }
}
