package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class TooManyFunctions(config: Config = Config.empty,
					   threshold: Int = DEFAULT_ACCEPTED_FUNCTIONS_IN_FILE) : ThresholdRule(config, threshold) {

	override val issue = Issue("TooManyFunctions",
			Severity.Maintainability,
			"Classes with many functions tend to do too many things and often come in conjunction with " +
					"large classes and can quickly become God classes. " +
					"Consider extracting methods to (new) classes better matching their responsibility.")

	private var amount: Int = 0

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		if (amount > threshold) {
			report(ThresholdedCodeSmell(issue, Entity.from(file), Metric("SIZE", amount, threshold)))
		}
		amount = 0
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		val amountOfFunctions = classOrObject.getBody()?.declarations
				?.filterIsInstance<KtNamedFunction>()
				?.size

		if (amountOfFunctions != null && amountOfFunctions > threshold) {
			report(ThresholdedCodeSmell(issue, Entity.from(classOrObject),
					Metric("SIZE", amountOfFunctions, threshold)))
		}

		super.visitClassOrObject(classOrObject)
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isTopLevel) {
			amount++
		}
	}

}

private val DEFAULT_ACCEPTED_FUNCTIONS_IN_FILE = 10
