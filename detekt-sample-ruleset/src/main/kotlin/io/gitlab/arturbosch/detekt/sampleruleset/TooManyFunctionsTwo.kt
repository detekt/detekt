package io.gitlab.arturbosch.detekt.sampleruleset

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class TooManyFunctionsTwo(config: Config) : Rule("TooManyFunctionsTwo", config) {

	private var amount: Int = 0

	override fun postVisit(context: Context, root: KtFile) {
		super.postVisit(context, root)
		if (amount > 10) {
			context.report(CodeSmell(
					issue = ISSUE,
					entity = Entity.from(root),
					metrics = listOf(Metric(type = "SIZE", value = amount, threshold = 10)),
					references = listOf())
			)
		}
	}

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		amount++
	}

	companion object {
		val ISSUE = Issue("TooManyFunctionsTwo",
				Issue.Severity.Maintainability,
				"Too many functions can make the maintainability of a file more costly")
	}
}