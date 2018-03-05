package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
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
 * Reports functions which have more parameters than a certain threshold (default: 6).
 *
 * @configuration threshold - maximum number of parameters (default: 6)
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
					"Prefer methods with short parameter lists.",
			Debt.TWENTY_MINS)

	private val ignoreDefaultParameters = valueOrDefault(IGNORE_DEFAULT_PARAMETERS, DEFAULT_IGNORE_DEFAULT_PARAMETERS)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.hasModifier(KtTokens.OVERRIDE_KEYWORD)) return
		val parameterList = function.valueParameterList
		val parameters = parameterList?.parameterCount()

		if (parameters != null && parameters >= threshold) {
			report(ThresholdedCodeSmell(issue,
					Entity.from(parameterList),
					Metric("SIZE", parameters, threshold),
					"The function ${function.nameAsSafeName} has too many parameters. The current threshold" +
							" is set to $threshold."))
		}
	}

	private fun KtParameterList.parameterCount(): Int {
		return if (ignoreDefaultParameters) {
			parameters.filter { !it.hasDefaultValue() }.size
		} else {
			parameters.size
		}
	}

	companion object {
		const val IGNORE_DEFAULT_PARAMETERS = "ignoreDefaultParameters"
		const val DEFAULT_ACCEPTED_PARAMETER_LENGTH = 6
		const val DEFAULT_IGNORE_DEFAULT_PARAMETERS = false
	}
}
