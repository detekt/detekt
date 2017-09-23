package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression

class ThrowsCount(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Restrict the number of throw statements in methods.",
			Debt.FIVE_MINS)

	private val max = valueOrDefault(MAX, 2)

	override fun visitNamedFunction(function: KtNamedFunction) {
		super.visitNamedFunction(function)
		if (!function.hasModifier(KtTokens.OVERRIDE_KEYWORD)) {
			val count = function.collectByType<KtThrowExpression>().count()
			if (count > max) {
				report(CodeSmell(issue, Entity.from(function)))
			}
		}
	}

	companion object {
		const val MAX = "max"
	}
}
