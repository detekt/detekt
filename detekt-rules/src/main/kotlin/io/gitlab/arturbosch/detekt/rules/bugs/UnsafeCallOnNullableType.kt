package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtUnaryExpression

/**
 * @author Ivan Balaksha
 */
class UnsafeCallOnNullableType : Rule() {
    override val issue: Issue = Issue("UnsafeCallOnNullableType", Severity.Defect, "")

    override fun visitUnaryExpression(expression: KtUnaryExpression) {
        super.visitUnaryExpression(expression)
        if (expression.operationToken == KtTokens.EXCLEXCL) {
            report(CodeSmell(issue, Entity.from(expression)))
        }
    }
}