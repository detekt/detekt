package io.gitlab.arturbosch.detekt.core

import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.CompilerResources
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleInstance
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.api.internal.whichJava
import io.gitlab.arturbosch.detekt.api.internal.whichOS
import io.gitlab.arturbosch.detekt.core.suppressors.buildSuppressors
import io.gitlab.arturbosch.detekt.core.suppressors.isSuppressedBy
import io.gitlab.arturbosch.detekt.core.util.isActiveOrDefault
import io.gitlab.arturbosch.detekt.core.util.shouldAnalyzeFile
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl
import java.net.URI
import java.nio.file.Path

internal class Analyzer(
    private val settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val processors: List<FileProcessListener>,
    private val bindingContext: BindingContext,
) {
    fun run(ktFiles: Collection<KtFile>): List<Issue> {
        val languageVersionSettings = settings.environment.configuration.languageVersionSettings

        val dataFlowValueFactory = DataFlowValueFactoryImpl(languageVersionSettings)
        val compilerResources = CompilerResources(languageVersionSettings, dataFlowValueFactory)
        val activeRules = getActiveRules(
            fullAnalysis = bindingContext != BindingContext.EMPTY,
            providers = providers,
            config = settings.config,
            log = settings::debug
        )
        return if (settings.spec.executionSpec.parallelAnalysis) {
            runAsync(ktFiles, activeRules, compilerResources)
        } else {
            runSync(ktFiles, activeRules, compilerResources)
        }
    }

    private fun runSync(
        ktFiles: Collection<KtFile>,
        rules: List<RuleDescriptor>,
        compilerResources: CompilerResources,
    ): List<Issue> =
        ktFiles.flatMap { file ->
            processors.forEach { it.onProcess(file) }
            val issues = runCatching { analyze(file, rules, compilerResources) }
                .onFailure { throwIllegalStateException(file, it) }
                .getOrDefault(emptyList())
            processors.forEach { it.onProcessComplete(file, issues) }
            issues
        }

    private fun runAsync(
        ktFiles: Collection<KtFile>,
        rules: List<RuleDescriptor>,
        compilerResources: CompilerResources,
    ): List<Issue> {
        val service = settings.taskPool
        val tasks: TaskList<List<Issue>?> = ktFiles.map { file ->
            service.task {
                processors.forEach { it.onProcess(file) }
                val issues = analyze(file, rules, compilerResources)
                processors.forEach { it.onProcessComplete(file, issues) }
                issues
            }.recover { throwIllegalStateException(file, it) }
        }
        return awaitAll(tasks).filterNotNull().flatten()
    }

    private fun analyze(
        file: KtFile,
        rules: List<RuleDescriptor>,
        compilerResources: CompilerResources,
    ): List<Issue> {
        val (correctableRules, otherRules) = rules.asSequence()
            .filter { ruleDescriptor ->
                ruleDescriptor.config.parent?.shouldAnalyzeFile(file, settings.spec.projectSpec.basePath) != false
            }
            .filter { ruleDescriptor ->
                ruleDescriptor.config.shouldAnalyzeFile(file, settings.spec.projectSpec.basePath)
            }
            .map { ruleDescriptor ->
                ruleDescriptor.toRuleInstance() to ruleDescriptor.ruleProvider(ruleDescriptor.config)
            }
            .filterNot { (ruleInstance, rule) ->
                file.isSuppressedBy(ruleInstance.id, rule.aliases, ruleInstance.ruleSetId)
            }
            .onEach { (_, rule) ->
                if (rule is RequiresFullAnalysis) {
                    rule.bindingContext = bindingContext
                }
            }
            .partition { (_, rule) -> rule.autoCorrect }

        return (correctableRules + otherRules).flatMap { (ruleInstance, rule) ->
            rule.visitFile(file, compilerResources)
                .filterNot {
                    it.entity.ktElement.isSuppressedBy(ruleInstance.id, rule.aliases, ruleInstance.ruleSetId)
                }
                .filterSuppressedFindings(rule, bindingContext)
                .map { it.toIssue(ruleInstance, ruleInstance.severity, settings.spec.projectSpec.basePath) }
        }
    }
}

private fun getActiveRules(
    fullAnalysis: Boolean,
    providers: List<RuleSetProvider>,
    config: Config,
    log: (() -> String) -> Unit = {},
): List<RuleDescriptor> = providers.asSequence()
    .map { it to config.subConfig(it.ruleSetId.value) }
    .filter { (_, ruleSetConfig) -> ruleSetConfig.isActiveOrDefault(true) }
    .map { (provider, ruleSetConfig) -> provider.instance() to ruleSetConfig }
    .flatMap { (ruleSet, ruleSetConfig) ->
        ruleSetConfig.subConfigKeys()
            .asSequence()
            .mapNotNull { ruleId -> extractRuleName(ruleId)?.let { ruleName -> ruleId to ruleName } }
            .mapNotNull { (ruleId, ruleName) ->
                ruleSet.rules[ruleName]?.let { ruleProvider ->
                    RuleDescriptor(
                        ruleProvider = ruleProvider,
                        config = ruleSetConfig.subConfig(ruleId),
                        ruleId = ruleId,
                        ruleSetId = ruleSet.id,
                    )
                }
            }
    }
    .filter { ruleDescriptor -> ruleDescriptor.config.isActiveOrDefault(false) }
    .filter { ruleDescriptor ->
        if (fullAnalysis) {
            true
        } else {
            val requiresFullAnalysis = ruleDescriptor.ruleProvider(Config.empty) is RequiresFullAnalysis
            if (requiresFullAnalysis) {
                log { "The rule '${ruleDescriptor.ruleId}' requires type resolution but it was run without it." }
            }
            !requiresFullAnalysis
        }
    }
    .toList()

internal fun extractRuleName(key: String): Rule.Name? =
    runCatching { Rule.Name(key.split("/", limit = 2).first()) }.getOrNull()

private data class RuleDescriptor(
    val ruleProvider: (Config) -> Rule,
    val config: Config,
    val ruleId: String,
    val ruleSetId: RuleSet.Id,
)

private fun List<Finding>.filterSuppressedFindings(rule: Rule, bindingContext: BindingContext): List<Finding> {
    val suppressors = buildSuppressors(rule, bindingContext)
    return if (suppressors.isNotEmpty()) {
        filter { finding -> !suppressors.any { suppressor -> suppressor.shouldSuppress(finding) } }
    } else {
        this
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

private fun Finding.toIssue(rule: RuleInstance, severity: Severity, basePath: Path): Issue = Issue(
    ruleInstance = rule,
    entity = entity.toIssue(basePath),
    references = references.map { it.toIssue(basePath) },
    message = message,
    severity = severity,
    suppressReasons = suppressReasons,
)

private fun Entity.toIssue(basePath: Path): Issue.Entity =
    Issue.Entity(signature, location.toIssue(basePath))

private fun Location.toIssue(basePath: Path): Issue.Location =
    Issue.Location(source, endSource, text, basePath.relativize(path))

private fun RuleDescriptor.toRuleInstance(): RuleInstance {
    val rule = ruleProvider(Config.empty)
    return RuleInstance(
        id = ruleId,
        ruleSetId = ruleSetId,
        url = URI("https://detekt.dev/docs/rules/${ruleSetId.value.lowercase()}#${rule.ruleName.value.lowercase()}"),
        description = rule.description,
        severity = rule.computeSeverity(),
    )
}

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

private val Rule.aliases: Set<String> get() = config.valueOrDefault(Config.ALIASES_KEY, emptyList<String>()).toSet()
