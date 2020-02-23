package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.core.rules.createRuleSet
import io.gitlab.arturbosch.detekt.core.rules.isActive
import io.gitlab.arturbosch.detekt.core.rules.shouldAnalyzeFile
import io.gitlab.arturbosch.detekt.core.rules.visitFile
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class Detektor(
    private val settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val processors: List<FileProcessListener> = emptyList()
) {

    private val config: Config = settings.config

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
            val findings = runCatching { file.analyze(bindingContext) }
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
                val findings = file.analyze(bindingContext)
                processors.forEach { it.onProcessComplete(file, findings) }
                findings
            }.recover {
                settings.error(createErrorMessage(file, it), it)
                emptyMap()
            }
        }
        return awaitAll(tasks).filterNotNull()
    }

    private fun KtFile.analyze(bindingContext: BindingContext): Map<RuleSetId, List<Finding>> =
        providers.asSequence()
            .filter { it.isActive(config) }
            .map { it.createRuleSet(config) }
            .filter { it.shouldAnalyzeFile(this, config) }
            .sortedBy { it.id }
            .map { ruleSet -> ruleSet.id to ruleSet.visitFile(this, bindingContext) }
            .toMergedMap()
}
