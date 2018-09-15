package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * Unnecessary `let` TODO
 *
 * <noncompliant>
 * TODO add example
 * </noncompliant>
 *
 * @author mishkun
 */
class UnnecessaryLet(config: Config) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"The `let` usage is unnecessary", Debt.FIVE_MINS)

	override fun visitCallExpression(expression: KtCallExpression) {
		super.visitCallExpression(expression)
		if (expression.isLetExpr()) {
			val lambdaExpr = expression.firstLambdaArg
			val lambdaParameter = lambdaExpr?.firstParameter
			val lambdaBody = lambdaExpr?.bodyExpression
			// we need to check lambdas with only one statement
			if (lambdaBody?.children?.size == 1) {
				// only dot qualified expressions can be unnecessary
				val firstExpr = lambdaBody.firstChild as? KtDotQualifiedExpression
				val exprReceiver = firstExpr?.receiverExpression

				if (exprReceiver != null) {
					val isLetWithImplicitParam = lambdaParameter == null && exprReceiver.textMatches(IT_LITERAL)
					val isLetWithExplicitParam = lambdaParameter != null && lambdaParameter.textMatches(exprReceiver)
					if (isLetWithExplicitParam || isLetWithImplicitParam) {
						report(CodeSmell(
								issue, Entity.from(expression),
								"let expression can be omitted"
						))
					}
				}
			}
		}
	}

}


private const val LET_LITERAL = "let"
private const val IT_LITERAL = "it"

private fun KtCallExpression.isLetExpr() = calleeExpression?.textMatches(LET_LITERAL) == true

private val KtCallExpression.firstLambdaArg get() = lambdaArguments.firstOrNull()?.getLambdaExpression()

private val KtLambdaExpression.firstParameter get() = valueParameters.firstOrNull()
