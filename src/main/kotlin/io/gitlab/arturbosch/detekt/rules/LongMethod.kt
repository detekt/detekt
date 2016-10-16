package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.MetricThresholdCodeSmellRule
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class LongMethod(threshold: Int = 10) : MetricThresholdCodeSmellRule("LongMethod", threshold) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		val body: KtBlockExpression? = function.bodyExpression.asBlockExpression()
		body?.let {
			val size = body.statements.size
			if (size > threshold) addFindings(CodeSmell(id, Location.of(function)))
		}
		super.visitNamedFunction(function)
	}
}