package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class LongMethod(config: Config = Config.empty, threshold: Int = 20) : ThresholdRule("LongMethod", config, threshold) {

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		val body: KtBlockExpression? = function.bodyExpression.asBlockExpression()
		body?.let {
			val size = body.statements.size
			if (size > threshold) context.report(
					ThresholdedCodeSmell(ISSUE, Entity.Companion.from(function), Metric("SIZE", size, threshold)))
		}
		super.visitNamedFunction(context, function)
	}

	companion object {
		val ISSUE = Issue("LongMethod", Issue.Severity.CodeSmell)
	}
}