package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameterList

/**
 * @author Artur Bosch
 */
class LongParameterList(config: Config = Config.empty,
						threshold: Int = DEFAULT_ACCEPTED_PARAMETER_LENGTH) : ThresholdRule(config, threshold) {

	override val issue = Issue("LongParameterList",
			Severity.Maintainability,
			"The more parameters a method has the more complex it is. Long parameter lists are often " +
					"used to control complex algorithms and violate the Single Responsibility Principle. " +
					"Prefer methods with short parameter lists.")

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.hasModifier(KtTokens.OVERRIDE_KEYWORD)) return
		function.valueParameterList?.checkThreshold()
	}

	private fun KtParameterList.checkThreshold() {
		val size = parameters.size
		if (size > threshold) {
			report(ThresholdedCodeSmell(issue,
					Entity.from(this),
					Metric("SIZE", size, threshold),
					message = ""))
		}
	}
}

private const val DEFAULT_ACCEPTED_PARAMETER_LENGTH = 5
