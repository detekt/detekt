package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression

class EqualsNullCall(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("EqualsWithHashCodeExist", Severity.Defect,
			"Equals() method is called with null as parameter. Consider using == to compare to null.")

	override fun visitCallExpression(expression: KtCallExpression) {
		if (expression.calleeExpression?.text == "equals" && hasNullParameter(expression)) {
			report(CodeSmell(issue, Entity.from(expression)))
		} else {
			super.visitCallExpression(expression)
		}
	}

	private fun hasNullParameter(expression: KtCallExpression): Boolean {
		val valueArguments = expression.valueArguments
		return valueArguments.size == 1 && valueArguments.first().text == "null"
	}
}
