package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression

class UnnecessaryConversionTemporary(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("UnnecessaryConversionTemporary", Severity.Performance,
			"Avoid temporary objects when converting primitive types to String")

	private val types: Set<String> = hashSetOf("Boolean", "Byte", "Short", "Integer", "Long", "Float", "Double")

	override fun visitCallExpression(expression: KtCallExpression) {
		if (types.contains(expression.calleeExpression?.text) && expression.nextSibling?.nextSibling?.text == "toString()") {
			report(CodeSmell(issue, Entity.from(expression)))
		}
	}
}
