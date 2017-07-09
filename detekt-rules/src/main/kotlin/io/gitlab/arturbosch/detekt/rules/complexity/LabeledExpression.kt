package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtExpressionWithLabel

/**
 * @author Ivan Balaksha
 */
class LabeledExpression(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue("LabeledExpression", Severity.Maintainability, "")

    override fun visitExpressionWithLabel(expression: KtExpressionWithLabel) {
        super.visitExpressionWithLabel(expression)
        expression.getLabelName()?.let {
            report(CodeSmell(issue, Entity.from(expression)))
        }
    }
}