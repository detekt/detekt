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
 * Functions which only contain a `return` statement can be collapsed to an expression body. This shortens and
 * cleans up the code.
 *
 * <noncompliant>
 * fun stuff(): Int {
 *     return 5
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun stuff() = 5
 *
 * fun stuff() {
 *     return
 *         moreStuff()
 *             .getStuff()
 *             .stuffStuff()
 * }
 * </compliant>
 *
 * @configuration includeLineWrapping - include return statements with line wraps in it (default: false)
 *
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author schalkms
 */
class ExpressionBodySyntax(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(
			javaClass.simpleName,
			Severity.Style,
			"Functions with exact one statement, the return statement," +
					" can be rewritten with ExpressionBodySyntax.",
			Debt.FIVE_MINS)

	private val includeLineWrapping = valueOrDefault(INCLUDE_LINE_WRAPPING, false)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.bodyExpression != null) {
			val body = function.bodyExpression!!
			body.singleReturnStatement()?.let { returnStmt ->
				if (includeLineWrapping || !containsWhiteSpace(returnStmt.returnedExpression)) {
					report(CodeSmell(issue, Entity.from(returnStmt), issue.description))
				}
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

	private fun containsWhiteSpace(expression: KtExpression?): Boolean {
		return expression?.children?.any {
			it.text.contains('\n')
		} == true
	}

	companion object {
		const val INCLUDE_LINE_WRAPPING = "includeLineWrapping"
	}

}
