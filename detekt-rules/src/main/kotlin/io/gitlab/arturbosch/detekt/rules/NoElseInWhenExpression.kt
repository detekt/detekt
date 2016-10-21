package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @author Artur Bosch
 */
class NoElseInWhenExpression(config: Config = Config.EMPTY) : Rule("NoElseInWhenExpression", Severity.Defect, config) {

	override fun visitWhenExpression(expression: KtWhenExpression) {
		if (expression.elseExpression == null) addFindings(CodeSmell(id, Location.of(expression)))
	}

}