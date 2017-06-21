package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class TooManyFunctions(config: Config = Config.empty, threshold: Int = 10) : ThresholdRule(config, threshold) {

	override val issue = Issue("TooManyFunctions", Severity.Maintainability, "")

	private var amount: Int = 0

	override fun visitFile(file: PsiFile) {// TODO
		super.visitFile(file)
		if (amount > threshold) {
			report(ThresholdedCodeSmell(issue, Entity.from(file),
					Metric("SIZE", value = amount, threshold = 10))
			)
		}
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		amount++
	}

}