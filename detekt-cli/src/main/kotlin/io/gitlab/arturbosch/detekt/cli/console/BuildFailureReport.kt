package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SingleAssign
import java.util.HashMap

/**
 * @author Artur Bosch
 */
class BuildFailureReport : ConsoleReport() {

	override val priority: Int = Int.MIN_VALUE

	private var weightsConfig: Config by SingleAssign()
	private var buildConfig: Config by SingleAssign()
	private var warningThreshold: Int by SingleAssign()
	private var failThreshold: Int by SingleAssign()
	private var maxIssues: Int by SingleAssign()

	companion object {
		private const val BUILD = "build"
		private const val WEIGHTS = "weights"
		private const val WARNING_THRESHOLD = "warningThreshold"
		private const val FAIL_THRESHOLD = "failThreshold"
		private const val MAX_ISSUES = "maxIssues"
	}

	override fun init(config: Config) {
		buildConfig = config.subConfig(BUILD)
		weightsConfig = buildConfig.subConfig(WEIGHTS)
		warningThreshold = buildConfig.valueOrDefault(WARNING_THRESHOLD, -1)
		failThreshold = buildConfig.valueOrDefault(FAIL_THRESHOLD, -1)
		maxIssues = buildConfig.valueOrDefault(MAX_ISSUES, -1)
	}

	override fun render(detektion: Detektion): String? {
		val smells = detektion.findings.flatMap { it.value }
		val ruleToRuleSetId = extractRuleToRuleSetIdMap(detektion)
		val amount = smells.map { it.weighted(ruleToRuleSetId) }.sum()

		checkDeprecation()
		return when {
			maxIssues.reached(amount) -> throw BuildFailure("Build failed with $amount weighted issues " +
					"(threshold defined was $maxIssues).")
			failThreshold.reached(amount) -> throw BuildFailure("Build failure threshold of " +
					"$failThreshold reached with $amount weighted smells!")
			warningThreshold.reached(amount) -> "Warning: $amount weighted code smells found. " +
					"Warning threshold is $warningThreshold and fail threshold is $failThreshold!"
			else -> null
		}
	}

	private fun checkDeprecation() {
		if (buildConfig.valueOrDefault(WARNING_THRESHOLD, Int.MIN_VALUE) != Int.MIN_VALUE
				|| buildConfig.valueOrDefault(FAIL_THRESHOLD, Int.MIN_VALUE) != Int.MIN_VALUE) {
			println("[Deprecation] - 'warningThreshold' and 'failThreshold' properties are deprecated." +
					" Please use the new 'maxIssues' config property.")
		}
	}

	private fun extractRuleToRuleSetIdMap(detektion: Detektion): HashMap<String, String> {
		return detektion.findings.mapValues { it.value.map { it.id }.toSet() }
				.map { map -> map.value.map { it to map.key }.toMap() }
				.fold(HashMap()) { result, map -> result.putAll(map); result }
	}

	private fun Finding.weighted(ids: Map<String, String>): Int {
		val key = ids[id] // entry of ID > entry of RuleSet ID > default weight 1
		return weightsConfig.valueOrDefault(id,
				if (key != null) weightsConfig.valueOrDefault(key, 1) else 1)
	}
}

internal fun Int.reached(amount: Int): Boolean = !(this == 0 && amount == 0) && this != -1 && this <= amount

class BuildFailure(override val message: String?) : RuntimeException(message)
