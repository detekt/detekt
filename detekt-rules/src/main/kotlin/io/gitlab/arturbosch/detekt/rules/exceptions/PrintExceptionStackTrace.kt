package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class PrintExceptionStackTrace(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("PrintExceptionStackTrace", Severity.CodeSmell,
			"Do not print an exception stack trace. Use a logger instead.")

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.catchBody?.collectByType<KtNameReferenceExpression>()?.forEach {
			if (it.text == catchClause.catchParameter?.name && hasPrintStacktraceCallExpression(it)) {
				report(CodeSmell(issue, Entity.from(it)))
			}
		}
	}

	private fun hasPrintStacktraceCallExpression(expression: KtNameReferenceExpression): Boolean {
		val methodCall = expression.nextSibling?.nextSibling
		return methodCall is KtCallExpression && methodCall.text.startsWith("printStackTrace(")
	}
}
