package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.SingleAssign
import java.util.HashMap

class BuildFailureReport : ConsoleReport() {

    override val priority: Int = Int.MIN_VALUE

    private var weightsConfig: Config by SingleAssign()
    private var buildConfig: Config by SingleAssign()
    private var maxIssues: Int by SingleAssign()

    companion object {
        private const val BUILD = "build"
        private const val WEIGHTS = "weights"
        private const val MAX_ISSUES = "maxIssues"
    }

    override fun init(config: Config) {
        buildConfig = config.subConfig(BUILD)
        weightsConfig = buildConfig.subConfig(WEIGHTS)
        maxIssues = buildConfig.valueOrDefault(MAX_ISSUES, -1)
    }

    override fun render(detektion: Detektion): String? {
        val smells = detektion.findings.flatMap { it.value }
        val ruleToRuleSetId = extractRuleToRuleSetIdMap(detektion)
        val amount = smells.map { it.weighted(ruleToRuleSetId) }.sum()

        return when {
            maxIssues.reached(amount) -> {
                val message = "Build failed with $amount weighted issues (threshold defined was $maxIssues)."
                println(message.red())
                throw BuildFailure(message)
            }
            amount > 0 && maxIssues != -1 -> {
                val message = "Build succeeded with $amount weighted issues (threshold defined was $maxIssues)."
                message.yellow()
            }
            else -> null
        }
    }

    private fun extractRuleToRuleSetIdMap(detektion: Detektion): HashMap<RuleSetId, String> {
        return detektion.findings.mapValues { it.value.map(Finding::id).toSet() }
            .map { map -> map.value.map { it to map.key }.toMap() }
            .fold(HashMap()) { result, map -> result.putAll(map); result }
    }

    private fun Finding.weighted(ids: Map<String, String>): Int {
        val key = ids[id] // entry of ID > entry of RuleSet ID > default weight 1
        return weightsConfig.valueOrDefault(
            id,
            if (key != null) weightsConfig.valueOrDefault(key, 1) else 1
        )
    }

    fun Int.reached(amount: Int): Boolean = !(this == 0 && amount == 0) && this != -1 && this <= amount
}

class BuildFailure(override val message: String?) : RuntimeException(message, null, true, false)
