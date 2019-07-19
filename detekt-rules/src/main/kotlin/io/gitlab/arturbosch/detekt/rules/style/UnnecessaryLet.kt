package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.IT_LITERAL
import io.gitlab.arturbosch.detekt.rules.LET_LITERAL
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * `let` expressions are used extensively in our code for null-checking and chaining functions,
 * but sometimes their usage should be replaced with a ordinary method/extension function call
 * to reduce visual complexity
 *
 * <noncompliant>
 * a.let { it.plus(1) } // can be replaced with `a.plus(1)`
 * a?.let { it.plus(1) } // can be replaced with `a?.plus(1)`
 * a.let { that -> that.plus(1) } // can be replaced with `a.plus(1)`
 * a?.let { that -> that.plus(1) } // can be replaced with `a?.plus(1)`
 * a?.let { that -> that.plus(1) }?.let { it.plus(1) } // can be replaced with `a?.plus(1)?.plus(1)`
 * </noncompliant>
 *
 * <compliant>
 * a?.let { print(it) }
 * a.let { print(it) }
 * a?.let { msg -> print(msg) }
 * a.let { msg -> print(msg) }
 * a?.let { 1.plus(it) } ?.let { msg -> print(msg) }
 * a?.let { it.plus(it) }
 * a?.let { param -> param.plus(param) }
 * </compliant>
 */
class UnnecessaryLet(config: Config) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "The `let` usage is unnecessary", Debt.FIVE_MINS)

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isLetExpr()) return

        val lambdaExpr = expression.firstLambdaArg
        val lambdaParameter = lambdaExpr?.firstParameter
        val lambdaBody = lambdaExpr?.bodyExpression

        if (lambdaBody.hasOnlyOneStatement()) {
            // only dot qualified expressions can be unnecessary
            val firstExpr = lambdaBody?.firstChild as? KtDotQualifiedExpression
            val exprReceiver = firstExpr?.receiverExpression

            if (exprReceiver != null) {
                val isLetWithImplicitParam = lambdaParameter == null && exprReceiver.textMatches(IT_LITERAL)
                val isLetWithExplicitParam = lambdaParameter != null && lambdaParameter.textMatches(exprReceiver)

                val hasOneRef = lambdaBody.countVarRefs(lambdaParameter?.text ?: IT_LITERAL) == 1

                if ((isLetWithExplicitParam || isLetWithImplicitParam) && hasOneRef) {
                    report(CodeSmell(
                            issue, Entity.from(expression),
                            "let expression can be omitted"
                    ))
                }
            }
        }
    }
}

private fun KtCallExpression.isLetExpr() = calleeExpression?.textMatches(LET_LITERAL) == true

private val KtCallExpression.firstLambdaArg get() = lambdaArguments.firstOrNull()?.getLambdaExpression()

private val KtLambdaExpression.firstParameter get() = valueParameters.firstOrNull()

private fun KtBlockExpression?.hasOnlyOneStatement() = this?.children?.size == 1

private fun PsiElement.countVarRefs(varName: String): Int =
        children.sumBy { it.countVarRefs(varName) + if (it.textMatches(varName)) 1 else 0 }
