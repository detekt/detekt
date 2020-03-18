package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.CorrectableCodeSmell
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSetId
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import java.util.HashMap

private val WEIGHTED_ISSUES_COUNT_KEY = Key.create<Int>("WEIGHTED_ISSUES_COUNT")
private const val BUILD = "build"
private const val WEIGHTS = "weights"
private const val MAX_ISSUES = "maxIssues"
private const val EXCLUDE_CORRECTABLE = "excludeCorrectable"

fun Config.maxIssues(): Int = subConfig(BUILD).valueOrDefault(MAX_ISSUES, -1)

fun Config.excludeCorrectable(): Boolean = subConfig(BUILD).valueOrDefault(EXCLUDE_CORRECTABLE, false)

fun Int.isValidAndSmallerOrEqual(amount: Int): Boolean =
    !(this == 0 && amount == 0) && this != -1 && this <= amount

fun Detektion.getOrComputeWeightedAmountOfIssues(config: Config): Int {
    val maybeAmount = this.getData(WEIGHTED_ISSUES_COUNT_KEY)
    if (maybeAmount != null) {
        return maybeAmount
    }

    val smells = filterAutoCorrectedIssues(config).flatMap { it.value }
    val ruleToRuleSetId = extractRuleToRuleSetIdMap(this)
    val weightsConfig = config.weightsConfig()

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

fun Detektion.filterEmptyIssues(config: Config): Map<RuleSetId, List<Finding>> {
    return this
        .filterAutoCorrectedIssues(config)
        .filter { it.value.isNotEmpty() }
}

fun Detektion.filterAutoCorrectedIssues(config: Config): Map<RuleSetId, List<Finding>> {
    if (!config.excludeCorrectable()) {
        return findings
    }
    val filteredFindings = HashMap<RuleSetId, List<Finding>>()
    findings.forEach { (ruleSetId, findingsList) ->
        val newFindingsList = findingsList.filter { finding ->
            val correctableCodeSmell = finding as? CorrectableCodeSmell
            correctableCodeSmell == null || !correctableCodeSmell.autoCorrectEnabled
        }
        filteredFindings[ruleSetId] = newFindingsList
    }
    return filteredFindings
}

private fun Config.weightsConfig(): Config = subConfig(BUILD).subConfig(WEIGHTS)

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
