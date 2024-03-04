package io.gitlab.arturbosch.detekt.core

import io.github.detekt.psi.absolutePath
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.CompilerResources
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.CorrectableCodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.isSuppressedBy
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.api.internal.whichJava
import io.gitlab.arturbosch.detekt.api.internal.whichOS
import io.gitlab.arturbosch.detekt.api.ruleId
import io.gitlab.arturbosch.detekt.core.config.AllRulesConfig
import io.gitlab.arturbosch.detekt.core.config.DisabledAutoCorrectConfig
import io.gitlab.arturbosch.detekt.core.config.validation.DeprecatedRule
import io.gitlab.arturbosch.detekt.core.config.validation.loadDeprecations
import io.gitlab.arturbosch.detekt.core.rules.associateRuleIdsToRuleSetIds
import io.gitlab.arturbosch.detekt.core.suppressors.buildSuppressors
import io.gitlab.arturbosch.detekt.core.tooling.getDefaultConfiguration
import io.gitlab.arturbosch.detekt.core.util.isActiveOrDefault
import io.gitlab.arturbosch.detekt.core.util.shouldAnalyzeFile
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl
import kotlin.reflect.full.hasAnnotation

private typealias FindingsResult = List<Map<RuleSet.Id, List<Finding2>>>

internal class Analyzer(
    private val settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val processors: List<FileProcessListener>
) {

    private val config: Config = settings.spec.workaroundConfiguration(settings.config)

    fun run(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext = BindingContext.EMPTY
    ): Map<RuleSet.Id, List<Finding2>> {
        val languageVersionSettings = settings.environment.configuration.languageVersionSettings

        val dataFlowValueFactory = DataFlowValueFactoryImpl(languageVersionSettings)
        val compilerResources = CompilerResources(languageVersionSettings, dataFlowValueFactory)
        val findingsPerFile: FindingsResult =
            if (settings.spec.executionSpec.parallelAnalysis) {
                runAsync(ktFiles, bindingContext, compilerResources)
            } else {
                runSync(ktFiles, bindingContext, compilerResources)
            }

        if (bindingContext == BindingContext.EMPTY) {
            warnAboutEnabledRequiresTypeResolutionRules()
        }

        val findingsPerRuleSet = HashMap<RuleSet.Id, List<Finding2>>()
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
        val tasks: TaskList<Map<RuleSet.Id, List<Finding2>>?> = ktFiles.map { file ->
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
    ): Map<RuleSet.Id, List<Finding2>> {
        val activeRuleSetsToRuleSetConfigs = providers.asSequence()
            .map { it to config.subConfig(it.ruleSetId.value) }
            .filter { (_, ruleSetConfig) -> ruleSetConfig.isActiveOrDefault(true) }
            .map { (provider, ruleSetConfig) -> provider.instance() to ruleSetConfig }
            .filter { (_, ruleSetConfig) -> ruleSetConfig.shouldAnalyzeFile(file) }
            .toList()

        val ruleIdsToRuleSetIds = associateRuleIdsToRuleSetIds(
            activeRuleSetsToRuleSetConfigs.map { (ruleSet, _) -> ruleSet }
        )

        val (correctableRules, otherRules) = activeRuleSetsToRuleSetConfigs
            .flatMap { (ruleSet, ruleSetConfig) ->
                ruleSet.rules
                    .asSequence()
                    .map { (ruleId, ruleProvider) -> ruleProvider to ruleSetConfig.subConfig(ruleId.value) }
                    .filter { (_, config) -> config.isActiveOrDefault(false) }
                    .filter { (_, config) -> config.shouldAnalyzeFile(file) }
                    .map { (ruleProvider, config) -> ruleProvider(config) }
                    .filter { rule -> !file.isSuppressedBy(rule.ruleId, rule.aliases, ruleSet.id) }
            }
            .filter { rule ->
                bindingContext != BindingContext.EMPTY || !rule::class.hasAnnotation<RequiresTypeResolution>()
            }
            .partition { rule -> rule.autoCorrect }

        val result = HashMap<RuleSet.Id, MutableList<Finding2>>()

        fun executeRules(rules: List<Rule>) {
            for (rule in rules) {
                val findings = rule.visitFile(file, bindingContext, compilerResources)
                    .filterSuppressedFindings(rule, bindingContext)
                for (finding in findings) {
                    val mappedRuleSet = checkNotNull(ruleIdsToRuleSetIds[rule.ruleId]) {
                        "Mapping for '${rule.ruleId}' expected."
                    }
                    result.computeIfAbsent(mappedRuleSet) { mutableListOf() }
                        .add(finding.toFinding2(Issue(rule.ruleId, rule.description), rule.computeSeverity()))
                }
            }
        }

        executeRules(correctableRules)
        executeRules(otherRules)

        return result
    }

    private fun warnAboutEnabledRequiresTypeResolutionRules() {
        providers.asSequence()
            .map { it to config.subConfig(it.ruleSetId.value) }
            .filter { (_, ruleSetConfig) -> ruleSetConfig.isActiveOrDefault(true) }
            .map { (provider, ruleSetConfig) -> provider.instance() to ruleSetConfig }
            .flatMap { (ruleSet, ruleSetConfig) ->
                ruleSet.rules
                    .asSequence()
                    .map { (ruleId, ruleProvider) -> ruleProvider to ruleSetConfig.subConfig(ruleId.value) }
                    .filter { (_, config) -> config.isActiveOrDefault(false) }
                    .map { (ruleProvider, config) -> ruleProvider(config) }
            }
            .filter { rule -> rule::class.hasAnnotation<RequiresTypeResolution>() }
            .forEach { rule ->
                settings.debug { "The rule '${rule.ruleId}' requires type resolution but it was run without it." }
            }
    }
}

private fun List<Finding>.filterSuppressedFindings(rule: Rule, bindingContext: BindingContext): List<Finding> {
    val suppressors = buildSuppressors(rule, bindingContext)
    return if (suppressors.isNotEmpty()) {
        filter { finding -> !suppressors.any { suppressor -> suppressor.shouldSuppress(finding) } }
    } else {
        this
    }
}

private fun MutableMap<RuleSet.Id, List<Finding2>>.mergeSmells(other: Map<RuleSet.Id, List<Finding2>>) {
    for ((key, findings) in other.entries) {
        merge(key, findings) { f1, f2 -> f1.plus(f2) }
    }
}

private fun throwIllegalStateException(file: KtFile, error: Throwable): Nothing {
    val message = """
        Analyzing ${file.absolutePath()} led to an exception.
        Location: ${error.stackTrace.firstOrNull()?.toString()}
        The original exception message was: ${error.localizedMessage}
        Running detekt '${whichDetekt()}' on Java '${whichJava()}' on OS '${whichOS()}'
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
        val defaultConfig = getDefaultConfiguration()
        val deprecatedRules = loadDeprecations().filterIsInstance<DeprecatedRule>().toSet()
        declaredConfig = AllRulesConfig(
            originalConfig = declaredConfig ?: defaultConfig,
            defaultConfig = defaultConfig,
            deprecatedRules = deprecatedRules
        )
    }

    if (!rulesSpec.autoCorrect) {
        declaredConfig = DisabledAutoCorrectConfig(declaredConfig ?: getDefaultConfiguration())
    }

    return declaredConfig ?: getDefaultConfiguration()
}

private fun Finding.toFinding2(issue: Issue, severity: Severity): Finding2 {
    return when (this) {
        is CorrectableCodeSmell -> Finding2Impl(issue, entity, message, references, severity, autoCorrectEnabled)

        is CodeSmell -> Finding2Impl(issue, entity, message, references, severity)

        else -> error("wtf?")
    }
}

private data class Finding2Impl(
    override val issue: Issue,
    override val entity: Entity,
    override val message: String,
    override val references: List<Entity>,
    override val severity: Severity,
    override val autoCorrectEnabled: Boolean = false,
) : Finding2

/**
 * Compute severity in the priority order:
 * - Severity of the rule
 * - Severity of the parent ruleset
 * - Default severity
 */
private fun Rule.computeSeverity(): Severity {
    val configValue: String = config.valueOrNull(Config.SEVERITY_KEY)
        ?: config.parent?.valueOrNull(Config.SEVERITY_KEY)
        ?: return Severity.Error
    return parseToSeverity(configValue)
}

internal fun parseToSeverity(severity: String): Severity {
    val lowercase = severity.lowercase()
    return Severity.entries.find { it.name.lowercase() == lowercase }
        ?: error("$severity is not a valid Severity. Allowed values are ${Severity.entries}")
}
