package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.BaseRule
import io.gitlab.arturbosch.detekt.core.rules.IdMapping
import io.gitlab.arturbosch.detekt.core.rules.associateRuleIdsToRuleSetIds
import io.gitlab.arturbosch.detekt.core.rules.isActive
import io.gitlab.arturbosch.detekt.core.rules.shouldAnalyzeFile
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

internal class Analyzer(
    private val settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val processors: List<FileProcessListener>
) {

    private val config: Config = settings.config
    private val idMapping: IdMapping =
        associateRuleIdsToRuleSetIds(providers.associate { it.ruleSetId to it.instance(config).rules })

    fun run(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext = BindingContext.EMPTY
    ): Map<RuleSetId, List<Finding>> {
        val findingsPerFile: FindingsResult =
            if (settings.spec.executionSpec.parallelAnalysis) {
                runAsync(ktFiles, bindingContext)
            } else {
                runSync(ktFiles, bindingContext)
            }

        val findingsPerRuleSet = HashMap<RuleSetId, List<Finding>>()
        for (findings in findingsPerFile) {
            findingsPerRuleSet.mergeSmells(findings)
        }
        return findingsPerRuleSet
    }

    private fun runSync(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext
    ): FindingsResult =
        ktFiles.map { file ->
            processors.forEach { it.onProcess(file) }
            val findings = runCatching { analyze(file, bindingContext) }
                .onFailure { settings.error(createErrorMessage(file, it), it) }
                .getOrDefault(emptyMap())
            processors.forEach { it.onProcessComplete(file, findings) }
            findings
        }

    private fun runAsync(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext
    ): FindingsResult {
        val service = settings.taskPool
        val tasks: TaskList<Map<RuleSetId, List<Finding>>?> = ktFiles.map { file ->
            service.task {
                processors.forEach { it.onProcess(file) }
                val findings = analyze(file, bindingContext)
                processors.forEach { it.onProcessComplete(file, findings) }
                findings
            }.recover {
                settings.error(createErrorMessage(file, it), it)
                emptyMap()
            }
        }
        return awaitAll(tasks).filterNotNull()
    }

    private fun analyze(file: KtFile, bindingContext: BindingContext): Map<RuleSetId, List<Finding>> {
        fun isCorrectable(rule: BaseRule): Boolean = when (rule) {
            is Rule -> rule.autoCorrect
            is MultiRule -> rule.rules.any { it.autoCorrect }
            else -> error("No other rule type expected.")
        }

        val (correctableRules, otherRules) = providers.asSequence()
            .map { it to config.subConfig(it.ruleSetId) }
            .filter { (_, ruleSetConfig) -> ruleSetConfig.isActive() }
            .mapLeft { provider, ruleSetConfig -> provider.instance(ruleSetConfig) }
            .filter { (_, ruleSetConfig) -> ruleSetConfig.shouldAnalyzeFile(file) }
            .flatMap { (ruleSet, _) -> ruleSet.rules.asSequence() }
            .partition { isCorrectable(it) }

        val result = HashMap<RuleSetId, MutableList<Finding>>()

        fun executeRules(rules: List<BaseRule>) {
            for (rule in rules) {
                rule.visitFile(file, bindingContext)
                for (finding in rule.findings) {
                    val mappedRuleSet = idMapping[finding.id] ?: error("Mapping for '${finding.id}' expected.")
                    result.putIfAbsent(mappedRuleSet, mutableListOf())
                    result[mappedRuleSet]!!.add(finding)
                }
            }
        }

        executeRules(correctableRules)
        executeRules(otherRules)

        return result
    }
}

private fun <T, U, R> Sequence<Pair<T, U>>.mapLeft(transform: (T, U) -> R): Sequence<Pair<R, U>> {
    return this.map { (first, second) -> transform(first, second) to second }
}
