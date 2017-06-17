package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.core.Detektion
import java.util.HashMap

/**
 * @author Artur Bosch
 */
class SmellThreshold(config: Config,
					 private val baselineFormat: DetektBaselineFormat?) {

	private val buildConfig = config.subConfig("build")
	private val weightsConfig = buildConfig.subConfig("weights")
	private val warning = buildConfig.valueOrDefault("warningThreshold", -1)
	private val fail = buildConfig.valueOrDefault("failThreshold", -1)

	class BuildFailure(override val message: String?) : RuntimeException(message)

	fun check(detektion: Detektion) {
		val smells = detektion.findings.flatMap { it.value }
		val ruleToRulesetId = extractRuleToRulesetIdMap(detektion)

		val filteredSmells = baselineFormat?.filter(smells) ?: smells
		val amount = filteredSmells.map { it.weighted(ruleToRulesetId) }.sum()

		println("\n")
		if (fail.reached(amount)) {
			throw BuildFailure("Build failure threshold of $fail reached with $amount weighted smells!")
		} else if (warning.reached(amount)) {
			println("Warning: $amount weighted code smells found. Warning threshold is $warning and fail threshold is $fail!")
		}

	}

	private fun extractRuleToRulesetIdMap(detektion: Detektion): HashMap<String, Issue> {
		return detektion.findings.mapValues { it.value.map { it.issue.id }.toSet() }
				.map { map -> map.value.map { it to map.key }.toMap() }
				.fold(HashMap<String, Issue>()) { result, map -> result.putAll(map); result }
	}

	private fun Int.reached(amount: Int): Boolean = this != -1 && this <= amount

	private fun Finding.weighted(ids: Map<String, Issue>): Int {
		val key = ids[issue.id] // entry of ID > entry of RulesetID > default weight 1
		return weightsConfig.valueOrDefault(issue.id,
				if (key != null) weightsConfig.valueOrDefault(key.id, 1) else 1)
	}

}
