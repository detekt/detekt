package io.gitlab.arturbosch.detekt.rules.style.movelambdaout

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

/**
 * In Kotlin functions the last lambda parameter of a function is a function then a lambda expression passed as the
 * corresponding argument can be placed outside the parentheses.
 * see [Passing trailing lambdas](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas).
 * Prefer the usage of trailing lambda.
 * <noncompliant>
 * fun test() {
 *     repeat(10, {
 *         println(it)
 *     })
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun test() {
 *     repeat(10) {
 *         println(it)
 *     }
 * }
 * </compliant>
 */
@RequiresTypeResolution
class UnnecessaryBracesAroundTrailingLambda(config: Config) : Rule(config) {
    override val issue: Issue = Issue(
        javaClass.simpleName,
        "Braces around trailing lambda is unnecessary.",
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (shouldReportUnnecessaryBracesAroundTrailingLambda(bindingContext, expression)) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(getIssueElement(expression)),
                    "Braces around trailing lambda can be removed."
                )
            )
        }
    }

    private fun getIssueElement(expression: KtCallExpression): PsiElement {
        return (expression.calleeExpression as? KtNameReferenceExpression) ?: expression
    }
}
