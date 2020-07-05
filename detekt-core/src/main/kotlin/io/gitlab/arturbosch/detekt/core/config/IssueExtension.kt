package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.core.reporting.BUILD
import io.gitlab.arturbosch.detekt.core.reporting.filterAutoCorrectedIssues
import org.jetbrains.kotlin.com.intellij.openapi.util.Key

private val WEIGHTED_ISSUES_COUNT_KEY = Key.create<Int>("WEIGHTED_ISSUES_COUNT")
private const val WEIGHTS = "weights"
internal const val MAX_ISSUES = "maxIssues"

internal fun Detektion.getOrComputeWeightedAmountOfIssues(config: Config): Int {
    val maybeAmount = this.getData(WEIGHTED_ISSUES_COUNT_KEY)
    if (maybeAmount != null) {
        return maybeAmount
    }

    val smells = filterAutoCorrectedIssues(config).flatMap { it.value }
    val ruleToRuleSetId = extractRuleToRuleSetIdMap(this)
    val weightsConfig = config.subConfig(BUILD).subConfig(WEIGHTS)

    fun Finding.weighted(): Int {
        val key = ruleToRuleSetId[id] // entry of ID > entry of RuleSet ID > default weight 1
        return weightsConfig.valueOrDefault(
            id,
            if (key != null) weightsConfig.valueOrDefault(key, 1) else 1
        )
    }

    val amount = smells.sumBy { it.weighted() }
    this.addData(WEIGHTED_ISSUES_COUNT_KEY, amount)
    return amount
}

private fun extractRuleToRuleSetIdMap(result: Detektion): Map<RuleId, RuleSetId> =
    result.findings
        .asSequence()
        .flatMap { (ruleSetId, findings) ->
            findings
                .asSequence()
                .map(Finding::id)
                .distinct()
                .map { it to ruleSetId }
        }
        .toMap()
