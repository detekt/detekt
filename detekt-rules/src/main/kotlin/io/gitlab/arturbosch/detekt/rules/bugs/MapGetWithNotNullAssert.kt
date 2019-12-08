package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtPostfixExpression

class MapGetWithNotNullAssert(config: Config) : Rule(config) {

    override val issue: Issue =
        Issue(
            "MapGetWithNotNullAssert",
            Severity.CodeSmell,
            "", // todo add description
            Debt.FIVE_MINS
        )

    override fun visitPostfixExpression(expression: KtPostfixExpression) {
        // todo implement
        super.visitPostfixExpression(expression)
    }
}
