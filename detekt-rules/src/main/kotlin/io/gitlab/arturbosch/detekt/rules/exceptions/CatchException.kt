package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class CatchException(config: Config = Config.empty) : ExceptionsRule("CatchException", config) {

	override fun visitCatchSection(context: Context, catchClause: KtCatchClause) {
		catchClause.addFindingIfExceptionClassMatchesExact(context, ISSUE) { "Exception" }
	}

	companion object {
		val ISSUE = Issue("CatchException", Issue.Severity.Maintainability)
	}
}