package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * This rule reports all exceptions that are caught and then later re-thrown without modification.
 * It ignores caught exception that are rethrown if there is work done before that.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         throw e
 *     }
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         print(e.message)
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
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         print(e)
 *         throw e
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
		val exceptionName = catchClause.catchParameter?.name ?: return
		val statements = catchClause.catchBody.asBlockExpression()?.statements ?: return
		for (stat in statements) {
			val throwExpression = stat as? KtThrowExpression
			if (throwExpression != null && throwExpression.thrownExpression?.text == exceptionName) {
				report(CodeSmell(issue, Entity.from(throwExpression), issue.description))
				break
			}
			if (isExceptionUsed(stat, exceptionName)) {
				break
			}
		}
	}

	private fun isExceptionUsed(stat: KtExpression, exceptionName: String): Boolean {
		return stat.collectByType<KtNameReferenceExpression>().any {
			it.text == exceptionName && (it.nextSibling == null || it.nextSibling.node.elementType != KtTokens.DOT)
		}
	}

}
