package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * @author Artur Bosch
 */
open class EmptyRule(id: String, config: Config) : Rule(id, config) {

	fun KtExpression.addFindingIfBlockExprIsEmpty(context: Context, issue: Issue) {
		this.asBlockExpression()?.statements?.let {
			if (it.isEmpty()) context.report(CodeSmell(issue, Entity.from(this)))
		}
	}
}