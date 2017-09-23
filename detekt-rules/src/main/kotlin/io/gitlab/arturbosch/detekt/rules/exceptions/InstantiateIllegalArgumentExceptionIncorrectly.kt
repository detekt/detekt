package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression

class InstantiateIllegalArgumentExceptionIncorrectly(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("InstantiateIllegalArgumentExceptionIncorrectly", Severity.Warning,
			"A call to the default constructor of an IllegalArgumentException was detected. " +
					"Instead one of the constructor overloads should be called. " +
					"This allows to provide more meaningful exceptions.",
			Debt.FIVE_MINS)

	override fun visitCallExpression(expression: KtCallExpression) {
		if (expression.calleeExpression?.text == "IllegalArgumentException" && expression.valueArguments.isEmpty()) {
			report(CodeSmell(issue, Entity.from(expression)))
		}
		super.visitCallExpression(expression)
	}
}

