package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.IT_LITERAL
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression

/**
 * Detects when an `also` block contains only `it`-started expressions.
 *
 * By refactoring the `also` block to an `apply` block makes it so that all `it`s can be removed
 * thus making the block more concise and easier to read.
 *
 * <noncompliant>
 * Buzz().also {
 *   it.init()
 *   it.block()
 * }
 * </noncompliant>
 *
 * <compliant>
 * Buzz().apply {
 *   init()
 *   block()
 * }
 *
 * // Also compliant
 * fun foo(a: Int): Int {
 *   return a.also { println(it) }
 * }
 * </compliant>
 */
class AlsoCouldBeApply(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "AlsoCouldBeApply",
        Severity.Style,
        "When an `also` block contains only `it`-started expressions, simplify it to the `apply` block.",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val callee = expression.calleeExpression?.takeIf { it.text == "also" } ?: return
        val lambda = expression.lambdaArguments.singleOrNull()?.getLambdaExpression()
            ?: expression.valueArguments.singleOrNull()?.getArgumentExpression()?.safeAs()
            ?: return
        val statements = lambda.bodyExpression?.statements.orEmpty().ifEmpty { return }
        if (statements.all { it.safeAs<KtQualifiedExpression>()?.receiverExpression?.text == IT_LITERAL }) {
            report(CodeSmell(issue, Entity.from(callee), issue.description))
        }
    }
}
