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
 * Reports functions which have more parameters then a certain threshold (default: 5).
 *
 * @configuration threshold - maximum number of parameters (default: 5)
 * @configuration ignoreDefaultParameters - ignore parameters that have a default value (default: false)
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author Serj Lotutovici
 */
class LongParameterList(config: Config = Config.empty,
						threshold: Int = DEFAULT_ACCEPTED_PARAMETER_LENGTH) : ThresholdRule(config, threshold) {

	override val issue = Issue("LongParameterList",
			Severity.Maintainability,
			"The more parameters a method has the more complex it is. Long parameter lists are often " +
					"used to control complex algorithms and violate the Single Responsibility Principle. " +
					"Prefer methods with short parameter lists.")

	private val ignoreDefaultParameters = valueOrDefault(IGNORE_DEFAULT_PARAMETERS, DEFAULT_IGNORE_DEFAULT_PARAMETERS)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.hasModifier(KtTokens.OVERRIDE_KEYWORD)) return
		function.valueParameterList?.checkThreshold()
	}

	private fun KtParameterList.checkThreshold() {
		val size = if (ignoreDefaultParameters) {
			parameters.filter { !it.hasDefaultValue() }.size
		} else {
			parameters.size
		}

		if (size > threshold) {
			report(ThresholdedCodeSmell(issue,
					Entity.from(this),
					Metric("SIZE", size, threshold),
					message = ""))
		}
	}

	companion object {
		const val IGNORE_DEFAULT_PARAMETERS = "ignoreDefaultParameters"
	}
}

private const val DEFAULT_ACCEPTED_PARAMETER_LENGTH = 5
private const val DEFAULT_IGNORE_DEFAULT_PARAMETERS = false
