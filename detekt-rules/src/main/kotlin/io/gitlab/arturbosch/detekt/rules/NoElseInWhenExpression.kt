package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @author Artur Bosch
 */
class NoElseInWhenExpression(config: Config = Config.empty) : Rule("NoElseInWhenExpression", Severity.Defect, config) {

	override fun visitWhenExpression(expression: KtWhenExpression) {
		if (expression.elseExpression == null) addFindings(CodeSmell(id, Entity.from(expression)))
	}

}