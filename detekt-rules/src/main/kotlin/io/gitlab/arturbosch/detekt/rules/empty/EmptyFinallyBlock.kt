package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Issue
import org.jetbrains.kotlin.psi.KtFinallySection

/**
 * @author Artur Bosch
 */
class EmptyFinallyBlock(config: Config) : EmptyRule("EmptyFinallyBlock", config = config) {

	override fun visitFinallySection(context: Context, finallySection: KtFinallySection) {
		finallySection.finalExpression?.addFindingIfBlockExprIsEmpty(context, ISSUE)
	}

	companion object {
		val ISSUE = Issue("EmptyFinallyBlock", Issue.Severity.Minor)
	}

}