package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression

class ThrowingExceptionsWithoutMessageOrCause(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("ThrowingExceptionsWithoutMessageOrCause", Severity.Warning,
			"A call to the default constructor of an exception was detected. " +
					"Instead one of the constructor overloads should be called. " +
					"This allows to provide more meaningful exceptions.",
			Debt.FIVE_MINS)

	private val exceptions = SplitPattern(valueOrDefault(EXCEPTIONS, ""))

	override fun visitCallExpression(expression: KtCallExpression) {
		val calleeExpressionText = expression.calleeExpression?.text
		if (exceptions.contains(calleeExpressionText) && expression.valueArguments.isEmpty()) {
			report(CodeSmell(issue, Entity.from(expression), message = ""))
		}
		super.visitCallExpression(expression)
	}

	companion object {
		const val EXCEPTIONS = "exceptions"
	}
}
