package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.internal.McCabeVisitor
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class ComplexMethod(config: Config = Config.empty,
					threshold: Int = DEFAULT_ACCEPTED_METHOD_COMPLEXITY) : ThresholdRule(config, threshold) {

	override val issue = Issue("ComplexMethod",
			Severity.Maintainability,
			"Prefer splitting up complex methods into smaller, " +
					"easier to understand methods.")

	override fun visitNamedFunction(function: KtNamedFunction) {
		val visitor = McCabeVisitor()
		visitor.visitNamedFunction(function)
		val mcc = visitor.mcc
		if (mcc > threshold) {
			report(ThresholdedCodeSmell(issue, Entity.from(function), Metric("MCC", mcc, threshold)))
		}
	}
}

private const val DEFAULT_ACCEPTED_METHOD_COMPLEXITY = 10
