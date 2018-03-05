package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * Exceptions should not be swallowed. This rule reports all instances where exceptions are `caught` and not correctly
 * passed into a newly thrown exception.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         throw MyException(e.message) // e is swallowed
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         throw MyException(e)
 *     }
 * }
 * </compliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class SwallowedException(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("SwallowedException", Severity.CodeSmell,
			"The caught exception is swallowed. The original exception could be lost.",
			Debt.TWENTY_MINS)

	override fun visitCatchSection(catchClause: KtCatchClause) {
		if (isExceptionSwallowed(catchClause) == true) {
			report(CodeSmell(issue, Entity.from(catchClause), issue.description))
		}
	}

	private fun isExceptionSwallowed(catchClause: KtCatchClause): Boolean? {
		val parameterName = catchClause.catchParameter?.name
		val throwExpressions = catchClause.catchBody?.collectByType<KtThrowExpression>()
		throwExpressions?.forEach { throwExpr ->
			val parameterNameReferences = throwExpr.thrownExpression?.collectByType<KtNameReferenceExpression>()?.filter {
				it.text == parameterName
			}
			return hasParameterReferences(parameterNameReferences)
		}
		return false
	}

	private fun hasParameterReferences(parameterNameReferences: List<KtNameReferenceExpression>?): Boolean {
		return parameterNameReferences != null
				&& parameterNameReferences.isNotEmpty()
				&& parameterNameReferences.all { callsMemberOfCaughtException(it) }
	}

	private fun callsMemberOfCaughtException(expression: KtNameReferenceExpression): Boolean {
		return expression.nextSibling?.text == "."
	}
}
