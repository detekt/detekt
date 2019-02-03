package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.linesOfCode
import io.gitlab.arturbosch.detekt.rules.parentOfType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.utils.addToStdlib.flattenTo
import java.util.IdentityHashMap

/**
 * Methods should have one responsibility. Long methods can indicate that a method handles too many cases at once.
 * Prefer smaller methods with clear names that describe their functionality clearly.
 *
 * Extract parts of the functionality of long methods into separate, smaller methods.
 *
 * @configuration threshold - maximum lines in a method (default: 20)
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class LongMethod(config: Config = Config.empty,
				 threshold: Int = DEFAULT_ACCEPTED_METHOD_LENGTH) : ThresholdRule(config, threshold) {

	override val issue = Issue("LongMethod",
			Severity.Maintainability,
			"One method should have one responsibility. Long methods tend to handle many things at once. " +
					"Prefer smaller methods to make them easier to understand.",
			Debt.TWENTY_MINS)

	private val functionToLinesCache = HashMap<KtNamedFunction, Int>()
	private val nestedFunctionTracking = IdentityHashMap<KtNamedFunction, HashSet<KtNamedFunction>>()

	override fun preVisit(root: KtFile) {
		functionToLinesCache.clear()
		nestedFunctionTracking.clear()
	}

	override fun postVisit(root: KtFile) {
		for ((function, lines) in functionToLinesCache) {
			if (lines >= threshold) {
				report(ThresholdedCodeSmell(issue,
						Entity.from(function),
						Metric("SIZE", lines, threshold),
						"The function ${function.nameAsSafeName} is too long. " +
								"The maximum length is $threshold."))
			}
		}
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		val lines = function.linesOfCode()
		functionToLinesCache[function] = lines
		function.parentOfType<KtNamedFunction>()
				?.let { nestedFunctionTracking.getOrPut(it) { HashSet() }.add(function) }
		super.visitNamedFunction(function)
		nestedFunctionTracking[function]
				?.fold(0) { acc, next -> acc + (functionToLinesCache[next] ?: 0) }
				?.let { functionToLinesCache[function] = lines - it }
	}

	private fun findAllNestedFunctions(startClass: KtNamedFunction): Sequence<KtNamedFunction> = sequence {
		var nestedFunctions = nestedFunctionTracking[startClass]
		while (!nestedFunctions.isNullOrEmpty()) {
			yieldAll(nestedFunctions)
			nestedFunctions = nestedFunctions.mapNotNull { nestedFunctionTracking[it] }.flattenTo(HashSet())
		}
	}

	companion object {
		const val DEFAULT_ACCEPTED_METHOD_LENGTH = 20
	}
}
