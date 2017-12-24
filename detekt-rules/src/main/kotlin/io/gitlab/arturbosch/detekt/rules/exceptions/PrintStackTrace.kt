package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

/**
 * This rule reports code that tries to print the stacktrace of an exception. Instead of simply printing a stacktrace
 * a better logging solution should be used.
 *
 * <noncompliant>
 * fun foo() {
 *     Thread.dumpStack()
 * }
 *
 * fun bar() {
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         e.printStackTrace()
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * val LOGGER = Logger.getLogger()
 *
 * fun bar() {
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         LOGGER.info(e)
 *     }
 * }
 * </compliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class PrintStackTrace(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("PrintStackTrace", Severity.CodeSmell,
			"Do not print an stack trace. " +
					"These debug statements should be replaced with a logger or removed.",
			Debt.FIVE_MINS)

	override fun visitCallExpression(expression: KtCallExpression) {
		val callNameExpression = expression.getCallNameExpression()
		if (callNameExpression?.text == "dumpStack"
				&& callNameExpression.getReceiverExpression()?.text == "Thread") {
			report(CodeSmell(issue, Entity.from(expression), message = ""))
		}
	}

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.catchBody?.collectByType<KtNameReferenceExpression>()?.forEach {
			if (it.text == catchClause.catchParameter?.name && hasPrintStacktraceCallExpression(it)) {
				report(CodeSmell(issue, Entity.from(it), message = ""))
			}
		}
	}

	private fun hasPrintStacktraceCallExpression(expression: KtNameReferenceExpression): Boolean {
		val methodCall = expression.nextSibling?.nextSibling
		return methodCall is KtCallExpression && methodCall.text.startsWith("printStackTrace(")
	}
}
