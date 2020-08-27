package io.gitlab.arturbosch.detekt.core

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.dsl.RuleConfiguration
import io.gitlab.arturbosch.detekt.api.internal.BaseRule
import io.gitlab.arturbosch.detekt.api.internal.CompilerResources
import io.gitlab.arturbosch.detekt.api.internal.DisabledAutoCorrectConfig
import io.gitlab.arturbosch.detekt.api.internal.FailFastConfig
import io.gitlab.arturbosch.detekt.core.config.DefaultConfig
import io.gitlab.arturbosch.detekt.core.rules.IdMapping
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl

internal class Analyzer(
    private val settings: ProcessingSettings,
    private val providers: List<RuleConfiguration<*>>,
    private val processors: List<FileProcessListener>
) {

    private val idMapping: IdMapping = providers.associate { it.factory().ruleId to it.ruleSet }

    fun run(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext = BindingContext.EMPTY
    ): Map<RuleSetId, List<Finding>> {
        val languageVersionSettings = settings.environment.configuration.languageVersionSettings
        @Suppress("DEPRECATION")
        val dataFlowValueFactory = DataFlowValueFactoryImpl(languageVersionSettings)
        val compilerResources = CompilerResources(languageVersionSettings, dataFlowValueFactory)
        val findingsPerFile: FindingsResult =
            if (settings.spec.executionSpec.parallelAnalysis) {
                runAsync(ktFiles, bindingContext, compilerResources)
            } else {
                runSync(ktFiles, bindingContext, compilerResources)
            }

        val findingsPerRuleSet = HashMap<RuleSetId, List<Finding>>()
        for (findings in findingsPerFile) {
            findingsPerRuleSet.mergeSmells(findings)
        }
        return findingsPerRuleSet
    }

    private fun runSync(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext,
        compilerResources: CompilerResources
    ): FindingsResult =
        ktFiles.map { file ->
            processors.forEach { it.onProcess(file, bindingContext) }
            val findings = runCatching { analyze(file, bindingContext, compilerResources) }
                .onFailure { settings.error(createErrorMessage(file, it), it) }
                .getOrDefault(emptyMap())
            processors.forEach { it.onProcessComplete(file, findings, bindingContext) }
            findings
        }

    private fun runAsync(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext,
        compilerResources: CompilerResources
    ): FindingsResult {
        val service = settings.taskPool
        val tasks: TaskList<Map<RuleSetId, List<Finding>>?> = ktFiles.map { file ->
            service.task {
                processors.forEach { it.onProcess(file, bindingContext) }
                val findings = analyze(file, bindingContext, compilerResources)
                processors.forEach { it.onProcessComplete(file, findings, bindingContext) }
                findings
            }.recover {
                settings.error(createErrorMessage(file, it), it)
                emptyMap()
            }
        }
        return awaitAll(tasks).filterNotNull()
    }

    private fun analyze(
        file: KtFile,
        bindingContext: BindingContext,
        compilerResources: CompilerResources
    ): Map<RuleSetId, List<Finding>> {
        fun isCorrectable(rule: BaseRule): Boolean = when (rule) {
            is Rule -> rule.autoCorrect
            is MultiRule -> rule.rules.any { it.autoCorrect }
            else -> error("No other rule type expected.")
        }

        val (correctableRules, otherRules) = providers
            .map { ruleConfiguration -> ruleConfiguration.factory() }
            .partition { isCorrectable(it) }

        val result = HashMap<RuleSetId, MutableList<Finding>>()

        fun executeRules(rules: List<BaseRule>) {
            for (rule in rules) {
                rule.visitFile(file, bindingContext, compilerResources)
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

internal fun ProcessingSpec.workaroundConfiguration(config: Config): Config = with(configSpec) {
    var declaredConfig: Config? = when {
        configPaths.isNotEmpty() -> config
        resources.isNotEmpty() -> config
        useDefaultConfig -> config
        else -> null
    }

    if (rulesSpec.activateExperimentalRules) {
        val defaultConfig = DefaultConfig.newInstance()
        declaredConfig = FailFastConfig(declaredConfig ?: defaultConfig, defaultConfig)
    }

    if (!rulesSpec.autoCorrect) {
        declaredConfig = DisabledAutoCorrectConfig(declaredConfig ?: DefaultConfig.newInstance())
    }

    return declaredConfig ?: DefaultConfig.newInstance()
}
