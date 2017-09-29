package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty

class ComplexInterface(config: Config = Config.empty,
					   threshold: Int = DEFAULT_LARGE_INTERFACE_COUNT) : ThresholdRule(config, threshold) {

	override val issue = Issue(javaClass.simpleName, Severity.Maintainability,
			"An interface contains too many functions and properties. " +
					"Large classes tend to handle many things at once. " +
					"An interface should have one responsibility. " +
					"Split up large interfaces into smaller ones that are easier to understand.")

	override fun visitClass(klass: KtClass) {
		if (klass.isInterface()) {
			val body = klass.getBody() ?: return
			val size = body.children.count { it is KtNamedFunction || it is KtProperty }
			if (size > threshold) {
				report(ThresholdedCodeSmell(issue, Entity.from(klass), Metric("SIZE: ", size, threshold)))
			}
		}
		super.visitClass(klass)
	}
}

private const val DEFAULT_LARGE_INTERFACE_COUNT = 10
