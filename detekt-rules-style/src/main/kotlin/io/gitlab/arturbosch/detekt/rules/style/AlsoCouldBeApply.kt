package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.IT_LITERAL
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

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

    override fun visitCallExpression(expression: KtCallExpression) {
        if (expression.calleeExpression?.text == "also") {
            val alsoExpression = expression.calleeExpression ?: return

            val lambda = expression.lambdaArguments.singleOrNull() ?: expression.valueArguments.single()
                .collectDescendantsOfType<KtLambdaExpression>()
                .single()
            val dotQualifiedsInLambda = lambda.collectDescendantsOfType<KtDotQualifiedExpression>()

            if (
                dotQualifiedsInLambda.isNotEmpty() &&
                dotQualifiedsInLambda.all { it.receiverExpression.textMatches(IT_LITERAL) }
            ) {
                report(CodeSmell(issue, Entity.from(alsoExpression), issue.description))
            }

            super.visitCallExpression(expression)
        } else {
            super.visitCallExpression(expression)
        }
    }
}
