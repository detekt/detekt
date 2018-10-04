package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtLambdaExpression


class ExplicitItLambdaParameter(val config: Config) : Rule(config) {
	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Declaring single explicit `it` parameter is redundant", Debt.FIVE_MINS)

	override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
		super.visitLambdaExpression(lambdaExpression)

		val isSingleParameterLambda = lambdaExpression.valueParameters.size == 1
		if (!isSingleParameterLambda) return

		val singleParameter = lambdaExpression.valueParameters.first()
		if (singleParameter.name == IT_LITERAL) {
			report(CodeSmell(
					issue, Entity.from(lambdaExpression),
					"explicit `it` parameter declaration can be omitted"
			))
		}
	}

	companion object {
		private const val IT_LITERAL = "it"
	}
}
