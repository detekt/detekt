package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtUnaryExpression

/**
 * @author Ivan Balaksha
 */
class UnsafeCallOnNullableType(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue("UnsafeCallOnNullableType", Severity.Defect, "")

    override fun visitUnaryExpression(expression: KtUnaryExpression) {
        super.visitUnaryExpression(expression)
        if (expression.operationToken == KtTokens.EXCLEXCL) {
            report(CodeSmell(issue, Entity.from(expression)))
        }
    }
}