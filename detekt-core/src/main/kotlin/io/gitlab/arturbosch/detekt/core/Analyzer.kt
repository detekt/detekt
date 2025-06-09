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
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.api.internal.whichJava
import io.gitlab.arturbosch.detekt.api.internal.whichOS
import io.gitlab.arturbosch.detekt.core.suppressors.buildSuppressors
import io.gitlab.arturbosch.detekt.core.suppressors.isSuppressedBy
import io.gitlab.arturbosch.detekt.core.util.shouldAnalyzeFile
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Path

internal class Analyzer(
    private val settings: ProcessingSettings,
    private val rules: List<RuleDescriptor>,
    private val processors: List<FileProcessListener>,
    private val bindingContext: BindingContext,
) {
    fun run(ktFiles: Collection<KtFile>): List<Issue> {
        val languageVersionSettings = settings.configuration.languageVersionSettings

        val compilerResources = CompilerResources(languageVersionSettings)
        return if (settings.spec.executionSpec.parallelAnalysis) {
            runAsync(ktFiles, compilerResources)
        } else {
            runSync(ktFiles, compilerResources)
        }
    }

    private fun runSync(
        ktFiles: Collection<KtFile>,
        compilerResources: CompilerResources,
    ): List<Issue> =
        ktFiles.flatMap { file ->
            processors.forEach { it.onProcess(file) }
            val issues = runCatching { analyze(file, compilerResources) }.fold(
                onSuccess = { it },
                onFailure = { throwIllegalStateException(file, it) }
            )
            processors.forEach { it.onProcessComplete(file, issues) }
            issues
        }

    private fun runAsync(
        ktFiles: Collection<KtFile>,
        compilerResources: CompilerResources,
    ): List<Issue> {
        val service = settings.taskPool
        val tasks: TaskList<List<Issue>?> = ktFiles.map { file ->
            service.task {
                processors.forEach { it.onProcess(file) }
                val issues = analyze(file, compilerResources)
                processors.forEach { it.onProcessComplete(file, issues) }
                issues
            }.recover { throwIllegalStateException(file, it) }
        }
        return awaitAll(tasks).filterNotNull().flatten()
    }

    private fun analyze(
        file: KtFile,
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
                ruleDescriptor.ruleInstance to ruleDescriptor.ruleProvider(ruleDescriptor.config)
            }
            .filterNot { (ruleInstance, rule) ->
                file.isSuppressedBy(ruleInstance.id, rule.aliases, ruleInstance.ruleSetId)
            }
            .onEach { (_, rule) -> if (rule is RequiresFullAnalysis) rule.setBindingContext(bindingContext) }
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

private val Rule.aliases: Set<String> get() = config.valueOrDefault(Config.ALIASES_KEY, emptyList<String>()).toSet()
