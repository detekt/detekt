package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.core.rules.IdMapping
import io.gitlab.arturbosch.detekt.core.rules.associateRuleIdsToRuleSetIds
import io.gitlab.arturbosch.detekt.core.rules.createRuleSet
import io.gitlab.arturbosch.detekt.core.rules.isActive
import io.gitlab.arturbosch.detekt.core.rules.shouldAnalyzeFile
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class Detektor(
    private val settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val processors: List<FileProcessListener> = emptyList()
) {

    private val config: Config = settings.config
    private val idMapping: IdMapping =
        associateRuleIdsToRuleSetIds(providers.associate { it.ruleSetId to it.instance(config).rules })

    fun run(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext = BindingContext.EMPTY
    ): Map<RuleSetId, List<Finding>> {
        val findingsPerFile: FindingsResult =
            if (settings.parallelCompilation) {
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
        val ruleSets = providers.asSequence()
            .filter { it.isActive(config) }
            .map { it.createRuleSet(config) }
            .filter { it.shouldAnalyzeFile(file, config) }
            .associate { it.id to it.rules }

        fun isCorrectable(rule: BaseRule): Boolean = when (rule) {
            is Rule -> rule.autoCorrect
            is MultiRule -> rule.rules.any { it.autoCorrect }
            else -> error("No other rule type expected.")
        }

        val (correctableRules, otherRules) =
            ruleSets.asSequence()
                .flatMap { (_, value) -> value.asSequence() }
                .partition { isCorrectable(it) }

        val result = HashMap<RuleSetId, MutableList<Finding>>()

        fun executeRules(rules: List<BaseRule>) {
            for (rule in rules) {
                rule.visitFile(file, bindingContext)
                for (finding in rule.findings) {
                    val mappedRuleSet = idMapping[finding.id]
                        ?: error("Mapping for '${finding.id}' expected.")
                    result.putIfAbsent(mappedRuleSet, ArrayList())
                    result[mappedRuleSet]?.add(finding)
                }
            }
        }

        executeRules(correctableRules)
        executeRules(otherRules)

        return result
    }
}
