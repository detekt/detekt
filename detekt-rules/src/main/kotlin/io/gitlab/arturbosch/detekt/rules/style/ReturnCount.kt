package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression

class ReturnCount(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Restrict the number of return statements in methods.", Debt.TEN_MINS)

	private val max = valueOrDefault(MAX, 2)

	override fun visitNamedFunction(function: KtNamedFunction) {
		super.visitNamedFunction(function)

		val numberOfReturns = function.collectByType<KtReturnExpression>().count()

		if (numberOfReturns > max) {
			report(CodeSmell(issue, Entity.from(function), message = ""))
		}
	}

	companion object {
		const val MAX = "max"
	}
}
