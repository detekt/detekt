package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.absolutePath
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
            .mapNotNull { it.buildRuleset(config) }
            .sortedBy { it.id }
            .distinctBy { it.id }
            .toList()
            .map { ruleSet -> ruleSet.id to ruleSet.accept(this, bindingContext) }
            .toMergedMap()

    private fun createErrorMessage(file: KtFile, error: Throwable): String =
        "Analyzing '${file.absolutePath()}' led to an exception.\n" +
            "The original exception message was: ${error.localizedMessage}\n" +
            "Running detekt '${whichDetekt()}' on Java '${whichJava()}' on OS '${whichOS()}'.\n" +
            "If the exception message does not help, please feel free to create an issue on our GitHub page."
}
