package dev.detekt.rules.style.movelambdaout

import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
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
class UnnecessaryBracesAroundTrailingLambda(config: Config) :
    Rule(
        config,
        "Braces around trailing lambda is unnecessary."
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (shouldReportUnnecessaryBracesAroundTrailingLambda(expression)) {
            report(
                Finding(
                    Entity.from(getIssueElement(expression)),
                    "Braces around trailing lambda can be removed."
                )
            )
        }
    }

    private fun getIssueElement(expression: KtCallExpression): PsiElement =
        (expression.calleeExpression as? KtNameReferenceExpression) ?: expression
}
