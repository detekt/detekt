package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * @author Artur Bosch
 */
open class EmptyRule(id: String, config: Config, severity: Severity = Rule.Severity.Minor) : Rule(id, severity, config) {

	fun KtExpression.addFindingIfBlockExprIsEmpty() {
		this.asBlockExpression()?.statements?.let {
			if (it.isEmpty()) addFindings(CodeSmell(id, severity, Entity.from(this)))
		}
	}
}