package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
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
class AlsoCouldBeApply(config: Config) :
    Rule(config, "When an `also` block contains only `it`-started expressions, simplify it to the `apply` block.") {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val callee = expression.calleeExpression?.takeIf { it.text == "also" } ?: return
        val lambda = expression.lambdaArguments.singleOrNull()?.getLambdaExpression()
            ?: expression.valueArguments.singleOrNull()?.getArgumentExpression() as? KtLambdaExpression
            ?: return
        val statements = lambda.bodyExpression?.statements.orEmpty().ifEmpty { return }
        if (statements.all { (it as? KtQualifiedExpression)?.receiverExpression?.text == "it" }) {
            report(Finding(Entity.from(callee), description))
        }
    }
}
