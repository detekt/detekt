package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression

/**
 *
 * <noncompliant>
 * fun stuff(): Int {
 *     return 5
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun stuff() = 5
 * </compliant>
 *
 * @author Artur Bosch
 */
class ExpressionBodySyntax(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(
			javaClass.simpleName,
			Severity.Style,
			"Functions with exact one statement, the return statement," +
					" can be rewritten with ExpressionBodySyntax.",
			Debt.FIVE_MINS)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.bodyExpression != null) {
			val body = function.bodyExpression!!
			body.singleReturnStatement()?.let { returnStmt ->
				report(CodeSmell(issue, Entity.from(returnStmt), message = ""))
			}
		}
	}

	private fun KtExpression.singleReturnStatement(): KtReturnExpression? {
		val statements = (this as? KtBlockExpression)?.statements
		return statements?.size?.let {
			if (it == 1 && statements[0] is KtReturnExpression) {
				return statements[0] as KtReturnExpression
			} else null
		}
	}

}
