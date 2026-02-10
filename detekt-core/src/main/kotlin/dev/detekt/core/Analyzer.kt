package dev.detekt.core

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.FileProcessListener
import dev.detekt.api.Finding
import dev.detekt.api.Issue
import dev.detekt.api.Location
import dev.detekt.api.Rule
import dev.detekt.api.RuleInstance
import dev.detekt.api.Severity
import dev.detekt.api.internal.whichDetekt
import dev.detekt.api.internal.whichJava
import dev.detekt.api.internal.whichOS
import dev.detekt.api.valueOrDefault
import dev.detekt.core.suppressors.buildSuppressors
import dev.detekt.core.suppressors.isSuppressedBy
import dev.detekt.core.util.shouldAnalyzeFile
import dev.detekt.psi.absolutePath
import dev.detekt.tooling.api.AnalysisMode
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

internal class Analyzer(
    private val settings: ProcessingSettings,
    private val rules: List<RuleDescriptor>,
    private val processors: List<FileProcessListener>,
    private val analysisMode: AnalysisMode,
) {
    fun run(ktFiles: Collection<KtFile>): List<Issue> {
        val languageVersionSettings = settings.configuration.languageVersionSettings

        return if (settings.spec.executionSpec.parallelAnalysis) {
            runAsync(ktFiles, languageVersionSettings)
        } else {
            runSync(ktFiles, languageVersionSettings)
        }
    }

    private fun runSync(ktFiles: Collection<KtFile>, languageVersionSettings: LanguageVersionSettings): List<Issue> =
        ktFiles.flatMap { file ->
            processors.forEach { it.onProcess(file) }
            val issues = runCatching { analyze(file, languageVersionSettings) }.fold(
                onSuccess = { it },
                onFailure = { throwIllegalStateException(file, it) }
            )
            processors.forEach { it.onProcessComplete(file, issues) }
            issues
        }

    private fun runAsync(ktFiles: Collection<KtFile>, languageVersionSettings: LanguageVersionSettings): List<Issue> {
        val service = settings.taskPool
        val tasks: TaskList<List<Issue>?> = ktFiles.map { file ->
            service.task {
                processors.forEach { it.onProcess(file) }
                val issues = analyze(file, languageVersionSettings)
                processors.forEach { it.onProcessComplete(file, issues) }
                issues
            }.recover { throwIllegalStateException(file, it) }
        }
        return awaitAll(tasks).filterNotNull().flatten()
    }

    private fun analyze(file: KtFile, languageVersionSettings: LanguageVersionSettings): List<Issue> {
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
            .partition { (_, rule) -> rule.autoCorrect }

        return (correctableRules + otherRules).flatMap { (ruleInstance, rule) ->
            rule.visitFile(file, languageVersionSettings)
                .filterNot {
                    it.entity.ktElement.isSuppressedBy(ruleInstance.id, rule.aliases, ruleInstance.ruleSetId)
                }
                .filterSuppressedFindings(rule, analysisMode)
                .map { it.toIssue(ruleInstance, ruleInstance.severity, settings.spec.projectSpec.basePath) }
        }
    }
}

private fun List<Finding>.filterSuppressedFindings(rule: Rule, analysisMode: AnalysisMode): List<Finding> {
    val suppressors = buildSuppressors(rule, analysisMode)
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
    Issue(
        ruleInstance = rule,
        entity = entity.toIssue(basePath),
        references = references.map { it.toIssue(basePath) },
        message = message,
        severity = severity,
        suppressReasons = suppressReasons,
    )

private fun Entity.toIssue(basePath: Path): Issue.Entity = Issue.Entity(signature, location.toIssue(basePath))

private fun Location.toIssue(basePath: Path): Issue.Location =
    Issue.Location(source, endSource, text, basePath.relativize(path))

private val Rule.aliases: Set<String> get() = config.valueOrDefault(Config.ALIASES_KEY, emptyList<String>()).toSet()
