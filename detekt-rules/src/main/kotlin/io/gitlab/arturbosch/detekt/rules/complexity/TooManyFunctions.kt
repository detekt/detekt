package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class TooManyFunctions(config: Config = Config.empty, threshold: Int = 10) :
		ThresholdRule("TooManyFunctions", config, threshold) {

	private var amount: Int = 0

	override fun postVisit(context: Context, root: KtFile) {
		if (amount > threshold) {
			context.report(ThresholdedCodeSmell(
					issue = ISSUE, entity = Entity.from(root),
					metric = Metric(type = "SIZE", value = amount, threshold = 10))
			)
		}
	}

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		amount++
	}

	companion object {
		val ISSUE = Issue("TooManyFunctions", Issue.Severity.CodeSmell)
	}
}