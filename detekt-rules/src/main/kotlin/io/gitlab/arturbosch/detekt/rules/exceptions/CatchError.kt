package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class CatchError(config: Config = Config.empty) : ExceptionsRule("CatchError", config) {

	override fun visitCatchSection(context: Context, catchClause: KtCatchClause) {
		catchClause.addFindingIfExceptionClassMatchesExact(context, ISSUE) { "Error" }
	}

	companion object {
		val ISSUE = Issue("CatchError", Issue.Severity.Maintainability)
	}
}