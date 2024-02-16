package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isNonNullCheck
import io.gitlab.arturbosch.detekt.rules.isNullCheck
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtPsiUtil

/**
 * `if` expressions that either check for not-null and return `null` in the false case or check for `null` and returns
 * `null` in the truthy case are better represented as `?.let {}` blocks.
 *
 * <noncompliant>
 * if (x != null) { transform(x) } else null
 * if (x == null) null else y
 * </noncompliant>
 *
 * <compliant>
 * x?.let { transform(it) }
 * x?.let { y }
 * </compliant>
 */
class UseLet(config: Config) : Rule(
    config,
    "Use `?.let {}` instead of if/else with a null block when checking for nullable values"
) {

    private fun isExpressionNull(branch: KtExpression?): Boolean {
        val statement = when (branch) {
            is KtBlockExpression -> if (branch.statements.size == 1) branch.statements.first() else null
            is KtConstantExpression -> branch
            else -> null
        }

        return statement?.let { KtPsiUtil.isNullConstant(it) } ?: false
    }

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)
        val condition = expression.condition as? KtBinaryExpression ?: return

        if (condition.isNullCheck() && isExpressionNull(expression.then)) {
            report(CodeSmell(Entity.from(expression), description))
        } else if (condition.isNonNullCheck() && isExpressionNull(expression.`else`)) {
            report(CodeSmell(Entity.from(expression), description))
        }
    }
}
