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
import org.jetbrains.kotlin.psi.KtThrowExpression

class RethrowCaughtException(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("RethrowCaughtException", Severity.CodeSmell,
			"Do not rethrow a caught exception of the same type.",
			Debt.FIVE_MINS)

	override fun visitCatchSection(catchClause: KtCatchClause) {
		val throwExpression = catchClause.catchBody?.collectByType<KtThrowExpression>()?.firstOrNull {
			it.thrownExpression?.text == catchClause.catchParameter?.name
		}
		if (throwExpression != null) {
			report(CodeSmell(issue, Entity.from(throwExpression)))
		}
	}
}
