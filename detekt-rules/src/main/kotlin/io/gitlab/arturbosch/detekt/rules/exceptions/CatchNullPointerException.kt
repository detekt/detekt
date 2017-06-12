package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class CatchNullPointerException(config: Config = Config.empty) : ExceptionsRule("CatchNullPointerException", config) {

	override fun visitCatchSection(context: Context, catchClause: KtCatchClause) {
		catchClause.addFindingIfExceptionClassMatchesExact(context, ISSUE) { "NullPointerException" }
	}

	companion object {
		val ISSUE = Issue("CatchNullPointerException", Issue.Severity.Maintainability)
	}

}