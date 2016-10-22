package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmellThresholdRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class LongMethod(config: Config = Config.EMPTY, threshold: Int = 20) : CodeSmellThresholdRule("LongMethod", config, threshold) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		val body: KtBlockExpression? = function.bodyExpression.asBlockExpression()
		body?.let {
			val size = body.statements.size
			if (size > threshold) addFindings(
					ThresholdedCodeSmell(id, Entity.from(function), Metric("SIZE", size, threshold)))
		}
		super.visitNamedFunction(function)
	}
}