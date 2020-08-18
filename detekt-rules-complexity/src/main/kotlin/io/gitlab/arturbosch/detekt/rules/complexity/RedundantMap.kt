package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.IT_LITERAL
import io.gitlab.arturbosch.detekt.rules.MAP_LITERAL
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * map { it } does not make sense. You can remove the operator without any impact.
 * <noncompliant>
 * listOf(1, 2).map { it }
 * </noncompliant>
 *
 * <compliant>
 * listOf(1, 2).map { it * 2 }
 * </compliant>
 * @since 1.11.0
 */
class RedundantMap(
    config: Config = Config.empty
) : Rule(config) {

    override val issue: Issue =
        Issue("RedundantMap", Severity.CodeSmell, "map { it } does not make sense", Debt.FIVE_MINS)

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)

        if (lambdaExpression.isLambdaJustIt() && lambdaExpression.isRootMap()) {
            report(CodeSmell(issue, Entity.from(lambdaExpression), "Operator can be removed"))
        }
    }

    private fun KtLambdaExpression.isLambdaJustIt() = this.bodyExpression?.chars == IT_LITERAL

    private fun KtLambdaExpression.isRootMap() = this.parent.parent.text.startsWith(MAP_LITERAL)
}
