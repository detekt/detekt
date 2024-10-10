package io.gitlab.arturbosch.detekt.core

import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.CodeSmell
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
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.api.internal.whichJava
import io.gitlab.arturbosch.detekt.api.internal.whichOS
import io.gitlab.arturbosch.detekt.core.suppressors.buildSuppressors
import io.gitlab.arturbosch.detekt.core.suppressors.isSuppressedBy
import io.gitlab.arturbosch.detekt.core.util.isActiveOrDefault
import io.gitlab.arturbosch.detekt.core.util.shouldAnalyzeFile
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl
import java.nio.file.Path
import kotlin.reflect.full.hasAnnotation

internal class Analyzer(
    private val settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val processors: List<FileProcessListener>
) {
    fun run(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext = BindingContext.EMPTY
    ): List<Issue> {
        val languageVersionSettings = settings.environment.configuration.languageVersionSettings

        val dataFlowValueFactory = DataFlowValueFactoryImpl(languageVersionSettings)
        val compilerResources = CompilerResources(languageVersionSettings, dataFlowValueFactory)
        if (bindingContext == BindingContext.EMPTY) {
            warnAboutEnabledRequiresFullAnalysisRules()
        }
        return if (settings.spec.executionSpec.parallelAnalysis) {
            runAsync(ktFiles, bindingContext, compilerResources)
        } else {
            runSync(ktFiles, bindingContext, compilerResources)
        }
    }

    private fun runSync(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext,
        compilerResources: CompilerResources
    ): List<Issue> =
        ktFiles.flatMap { file ->
            processors.forEach { it.onProcess(file) }
            val issues = runCatching { analyze(file, bindingContext, compilerResources) }
                .onFailure { throwIllegalStateException(file, it) }
                .getOrDefault(emptyList())
            processors.forEach { it.onProcessComplete(file, issues) }
            issues
        }

    private fun runAsync(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext,
        compilerResources: CompilerResources
    ): List<Issue> {
        val service = settings.taskPool
        val tasks: TaskList<List<Issue>?> = ktFiles.map { file ->
            service.task {
                processors.forEach { it.onProcess(file) }
                val issues = analyze(file, bindingContext, compilerResources)
                processors.forEach { it.onProcessComplete(file, issues) }
                issues
            }.recover { throwIllegalStateException(file, it) }
        }
        return awaitAll(tasks).filterNotNull().flatten()
    }

    private fun analyze(
        file: KtFile,
        bindingContext: BindingContext,
        compilerResources: CompilerResources
    ): List<Issue> {
        val activeRuleSetsToRuleSetConfigs = providers.asSequence()
            .map { it to settings.config.subConfig(it.ruleSetId.value) }
            .filter { (_, ruleSetConfig) -> ruleSetConfig.isActiveOrDefault(true) }
            .map { (provider, ruleSetConfig) -> provider.instance() to ruleSetConfig }
            .filter { (_, ruleSetConfig) -> ruleSetConfig.shouldAnalyzeFile(file, settings.spec.projectSpec.basePath) }

        val (correctableRules, otherRules) = activeRuleSetsToRuleSetConfigs
            .flatMap { (ruleSet, ruleSetConfig) ->
                ruleSetConfig.subConfigKeys()
                    .asSequence()
                    .mapNotNull { ruleId ->
                        extractRuleName(ruleId)?.let { ruleName -> ruleId to ruleName }
                    }
                    .mapNotNull { (ruleId, ruleName) ->
                        ruleSet.rules[ruleName]?.let { ruleProvider ->
                            RuleDescriptor(
                                ruleProvider = ruleProvider,
                                config = ruleSetConfig.subConfig(ruleId),
                                ruleId = ruleId,
                            )
                        }
                    }
                    .filter { (_, config, _) -> config.isActiveOrDefault(false) }
                    .filter { (_, config, _) -> config.shouldAnalyzeFile(file, settings.spec.projectSpec.basePath) }
                    .map { (ruleProvider, config, ruleId) ->
                        val rule = ruleProvider(config)
                        rule.toRuleInstance(ruleId, ruleSet.id) to rule
                    }
            }
            .filterNot { (ruleInstance, rule) ->
                file.isSuppressedBy(ruleInstance.id, rule.aliases, ruleInstance.ruleSetId)
            }
            .filter { (_, rule) ->
                bindingContext != BindingContext.EMPTY || !rule::class.hasAnnotation<RequiresFullAnalysis>()
            }
            .partition { (_, rule) -> rule.autoCorrect }

        return (correctableRules + otherRules).flatMap { (ruleInstance, rule) ->
            rule.visitFile(file, bindingContext, compilerResources)
                .filterNot {
                    it.entity.ktElement.isSuppressedBy(ruleInstance.id, rule.aliases, ruleInstance.ruleSetId)
                }
                .filterSuppressedFindings(rule, bindingContext)
                .map { it.toIssue(ruleInstance, rule.computeSeverity(), settings.spec.projectSpec.basePath) }
        }
    }

    private fun warnAboutEnabledRequiresFullAnalysisRules() {
        providers.asSequence()
            .map { it to settings.config.subConfig(it.ruleSetId.value) }
            .filter { (_, ruleSetConfig) -> ruleSetConfig.isActiveOrDefault(true) }
            .map { (provider, ruleSetConfig) -> provider.instance() to ruleSetConfig }
            .flatMap { (ruleSet, ruleSetConfig) ->
                ruleSet.rules
                    .asSequence()
                    .map { (ruleName, ruleProvider) -> ruleProvider to ruleSetConfig.subConfig(ruleName.value) }
                    .filter { (_, config) -> config.isActiveOrDefault(false) }
                    .map { (ruleProvider, config) -> ruleProvider(config) }
            }
            .filter { rule -> rule::class.hasAnnotation<RequiresFullAnalysis>() }
            .forEach { rule ->
                settings.debug { "The rule '${rule.ruleName}' requires type resolution but it was run without it." }
            }
    }
}

internal fun extractRuleName(key: String): Rule.Name? =
    runCatching { Rule.Name(key.split("/", limit = 2).first()) }.getOrNull()

private data class RuleDescriptor(val ruleProvider: (Config) -> Rule, val config: Config, val ruleId: String)

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

private fun Finding.toIssue(rule: RuleInstance, severity: Severity, basePath: Path): Issue =
    when (this) {
        is CodeSmell -> IssueImpl(
            rule,
            entity.toIssue(basePath),
            message,
            references.map { it.toIssue(basePath) },
            severity,
            suppressReasons,
        )

        else -> error("wtf?")
    }

private fun Entity.toIssue(basePath: Path): Issue.Entity =
    IssueImpl.Entity(name, signature, location.toIssue(basePath), ktElement)

private fun Location.toIssue(basePath: Path): Issue.Location =
    IssueImpl.Location(source, endSource, text, basePath.relativize(path))

private fun Rule.toRuleInstance(id: String, ruleSetId: RuleSet.Id): RuleInstance =
    RuleInstanceImpl(id, ruleName, ruleSetId, description)

private data class IssueImpl(
    override val ruleInstance: RuleInstance,
    override val entity: Issue.Entity,
    override val message: String,
    override val references: List<Issue.Entity>,
    override val severity: Severity,
    override val suppressReasons: List<String>,
) : Issue {
    data class Entity(
        override val name: String,
        override val signature: String,
        override val location: Issue.Location,
        override val ktElement: KtElement
    ) : Issue.Entity

    data class Location(
        override val source: SourceLocation,
        override val endSource: SourceLocation,
        override val text: TextLocation,
        override val path: Path
    ) : Issue.Location {
        init {
            require(!path.isAbsolute) { "Path should be always relative" }
        }
    }
}

private data class RuleInstanceImpl(
    override val id: String,
    override val name: Rule.Name,
    override val ruleSetId: RuleSet.Id,
    override val description: String,
) : RuleInstance

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
