package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtValueArgumentList

/**
 * @author Ivan Balaksha
 */
class SpreadOperator(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("SpreadOperator",
			Severity.Performance,
			"Using spread operator, which causes a full copy of the array to be created before calling a method, " +
					"has a very high performance penalty.")

	override fun visitValueArgumentList(list: KtValueArgumentList) {
		super.visitValueArgumentList(list)
		list.arguments.filter { it.getSpreadElement() != null }
				.forEach {
					report(CodeSmell(issue, Entity.from(list)))
				}
	}
}
