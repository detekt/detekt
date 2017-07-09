package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtValueArgumentList

/**
 * @author Ivan Balaksha
 */
class SpreadOperator(config: Config = Config.empty) : Rule() {
	override val issue: Issue = Issue("SpreadOperator", Severity.Performance, "")

	override fun visitValueArgumentList(list: KtValueArgumentList) {
		super.visitValueArgumentList(list)
		list.arguments.filter { it.firstChild.textMatches(KtTokens.MUL.value) }
				.forEach {
					report(CodeSmell(issue, Entity.from(list)))
				}
	}
}