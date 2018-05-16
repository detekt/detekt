package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * This rule reports all exceptions that are caught and then later re-thrown without modification.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         throw e
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         throw MyException(e)
 *     }
 * }
 * </compliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class RethrowCaughtException(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("RethrowCaughtException", Severity.CodeSmell,
			"Do not rethrow a caught exception of the same type.",
			Debt.FIVE_MINS)

	override fun visitCatchSection(catchClause: KtCatchClause) {
		val statements = catchClause.catchBody.asBlockExpression()?.statements ?: return
		if (statements.isNotEmpty()) {
			val throwExpression = statements[0] as? KtThrowExpression
			if (throwExpression != null && throwExpression.thrownExpression?.text == catchClause.catchParameter?.name) {
				report(CodeSmell(issue, Entity.from(throwExpression), issue.description))
			}
		}
	}
}
