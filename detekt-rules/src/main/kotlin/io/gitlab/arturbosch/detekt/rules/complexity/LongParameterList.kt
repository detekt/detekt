package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameterList

/**
 * @author Artur Bosch
 */
class LongParameterList(config: Config = Config.empty, threshold: Int = 5) :
		ThresholdRule("LongParameterList", config, threshold) {

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		if (function.hasModifier(KtTokens.OVERRIDE_KEYWORD)) return
		function.valueParameterList?.checkThreshold(context)
	}

	private fun KtParameterList.checkThreshold(context: Context) {
		val size = parameters.size
		if (size > threshold) {
			context.report(ThresholdedCodeSmell(ISSUE, Entity.Companion.from(this), Metric("SIZE", size, threshold)))
		}
	}

	companion object {
		val ISSUE = Issue("LongParameterList", Issue.Severity.CodeSmell)
	}
}