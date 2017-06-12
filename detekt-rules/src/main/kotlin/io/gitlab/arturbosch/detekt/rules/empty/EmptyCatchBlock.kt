package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class EmptyCatchBlock(config: Config) : EmptyRule("EmptyCatchBlock", config = config) {

	override fun visitCatchSection(context: Context, catchClause: KtCatchClause) {
		catchClause.catchBody?.addFindingIfBlockExprIsEmpty(context, ISSUE)
	}

	companion object {
		val ISSUE = Issue("EmptyCatchBlock", Issue.Severity.Minor)
	}
}
