package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmellThresholdRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class TooManyFunctions(config: Config = Config.empty, threshold: Int = 10) : CodeSmellThresholdRule("TooManyFunctions", config, threshold) {

	private var amount: Int = 0

	override fun visitFile(file: PsiFile) {// TODO
		super.visitFile(file)
		if (amount > threshold) {
			addFindings(ThresholdedCodeSmell(
					id = id, severity = severity, entity = Entity.from(file),
					metric = Metric(type = "SIZE", value = amount, threshold = 10))
			)
		}
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		amount++
	}

}