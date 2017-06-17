package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class CatchIndexOutOfBoundsException(config: Config = Config.empty) :
		ExceptionsRule("CatchIndexOutOfBoundsException", config) {

	override fun visitCatchSection(context: Context, catchClause: KtCatchClause) {
		catchClause.addFindingIfExceptionClassMatchesExact(context, ISSUE) { "IndexOutOfBoundsException" }
	}

	companion object {
		val ISSUE = Issue("CatchIndexOutOfBoundsException", Issue.Severity.Maintainability)
	}
}