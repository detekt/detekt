package io.gitlab.arturbosch.detekt.core

import io.github.detekt.psi.absolutePath
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.BaseRule
import io.gitlab.arturbosch.detekt.api.internal.CompilerResources
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.api.internal.whichJava
import io.gitlab.arturbosch.detekt.api.internal.whichOS
import io.gitlab.arturbosch.detekt.core.config.DefaultConfig
import io.gitlab.arturbosch.detekt.core.config.DisabledAutoCorrectConfig
import io.gitlab.arturbosch.detekt.core.config.AllRulesConfig
import io.gitlab.arturbosch.detekt.core.rules.IdMapping
import io.gitlab.arturbosch.detekt.core.rules.associateRuleIdsToRuleSetIds
import io.gitlab.arturbosch.detekt.core.rules.isActive
import io.gitlab.arturbosch.detekt.core.rules.shouldAnalyzeFile
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl

private typealias FindingsResult = List<Map<RuleSetId, List<Finding>>>

internal class Analyzer(
    private val settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val processors: List<FileProcessListener>
) {

    private val config: Config = settings.spec.workaroundConfiguration(settings.config)
    private val idMapping: IdMapping =
        associateRuleIdsToRuleSetIds(providers.associate { it.ruleSetId to it.instance(config).rules })

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
                .onFailure { throwIllegalStateException(file, it) }
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
            }.recover { throwIllegalStateException(file, it) }
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
                rule.visitFile(file, bindingContext, compilerResources)
                for (finding in rule.findings) {
                    val mappedRuleSet = checkNotNull(idMapping[finding.id]) { "Mapping for '${finding.id}' expected." }
                    result.computeIfAbsent(mappedRuleSet) { mutableListOf() }
                        .add(finding)
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

private fun MutableMap<String, List<Finding>>.mergeSmells(other: Map<String, List<Finding>>) {
    for ((key, findings) in other.entries) {
        merge(key, findings) { f1, f2 -> f1.plus(f2) }
    }
}

private fun throwIllegalStateException(file: KtFile, error: Throwable): Nothing {
    val message = """
    Analyzing ${file.absolutePath()} led to an exception. 
    The original exception message was: ${error.localizedMessage}
    Running detekt '${whichDetekt() ?: "unknown"}' on Java '${whichJava()}' on OS '${whichOS()}'
    If the exception message does not help, please feel free to create an issue on our GitHub page.
    """.trimIndent()
    throw IllegalStateException(message, error)
}

internal fun ProcessingSpec.workaroundConfiguration(config: Config): Config = with(configSpec) {
    var declaredConfig: Config? = when {
        configPaths.isNotEmpty() -> config
        resources.isNotEmpty() -> config
        useDefaultConfig -> config
        else -> null
    }

    if (rulesSpec.activateAllRules) {
        val defaultConfig = DefaultConfig.newInstance()
        declaredConfig = AllRulesConfig(declaredConfig ?: defaultConfig, defaultConfig)
    }

    if (!rulesSpec.autoCorrect) {
        declaredConfig = DisabledAutoCorrectConfig(declaredConfig ?: DefaultConfig.newInstance())
    }

    return declaredConfig ?: DefaultConfig.newInstance()
}
