package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.CodeSmell
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf
import org.jetbrains.kotlin.psi.unpackFunctionLiteral

/**
 * Unnecessary filter add complexity to the code and accomplish noting. They should be removed.
 *
 * <noncompliant>
 * val x = listOf(1, 2, 3)
 *      .filter { it > 1 }
 *      .count()
 *
 * val x = listOf(1, 2, 3)
 *      .filter { it > 1 }
 *      .isEmpty()
 * </noncompliant>
 *
 * <compliant>
 * val x = listOf(1, 2, 3)
 *      .count { it > 2 }
 * }
 *
 * val x = listOf(1, 2, 3)
 *      .none { it > 1 }
 *
 * </compliant>
 *
 * @requiresTypeResolution
 */
class UnnecessaryFilter(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue("UnnecessaryFilter", Severity.Style,
        "UnnecessaryFilter",
        Debt.FIVE_MINS)

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val calleeExpression = expression.calleeExpression
        if (calleeExpression?.text != "filter") return

        expression.checkNextLeaf("size")
        expression.checkNextLeaf("count")
        expression.checkNextLeaf(
            "isEmpty", "any")
        expression.checkNextLeaf(
            "isNotEmpty", "none")
    }

    private fun KtCallExpression.checkNextLeaf(leafName: String, correctOperator: String = leafName) {
        val hasNextLeaf = this.nextLeaf { it.text == leafName } != null

        if (hasNextLeaf) {
            report(CodeSmell(issue, Entity.from(this),
                "$this can be replace by $correctOperator { ${this.lambda()?.text}}"))
        }
    }

    private fun KtCallExpression.lambda(): KtLambdaExpression? {
        val argument = lambdaArguments.singleOrNull() ?: valueArguments.singleOrNull()
        return argument?.getArgumentExpression()?.unpackFunctionLiteral()
    }
}
