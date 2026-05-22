package dev.detekt.core.tooling

import dev.detekt.api.Config
import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.RuleSetProvider
import dev.detekt.core.Analyzer
import dev.detekt.core.FileProcessorLocator
import dev.detekt.core.ProcessingSettings
import dev.detekt.core.config.validation.checkConfiguration
import dev.detekt.core.extensions.handleReportingExtensions
import dev.detekt.core.getRules
import dev.detekt.core.parser.DetektMessageCollector
import dev.detekt.core.rules.createRuleProviders
import dev.detekt.tooling.api.AnalysisMode
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.diagnostics.KaSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocationWithRange
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils.getLineAndColumnRangeInPsiFile
import org.jetbrains.kotlin.psi.KtFile

internal class Lifecycle(
    val baselineConfig: Config,
    val settings: ProcessingSettings,
    val processorsProvider: () -> List<FileProcessListener> = { FileProcessorLocator(settings).load() },
    val ruleSetsProvider: () -> List<RuleSetProvider> = { settings.createRuleProviders() },
) {
    fun analyze(): Detektion {
        Tracer.trace("Main", "Validate configuration") { checkConfiguration(settings, baselineConfig) }
        val filesToAnalyze = Tracer.trace("Main", "Parsing files") { settings.ktFiles }
        if (settings.spec.projectSpec.analysisMode == AnalysisMode.full) {
            Tracer.trace("Main", "Validate classpath") { validateClasspath(filesToAnalyze) }
        }
        val analysisMode = settings.spec.projectSpec.analysisMode
        val (processors, rules) = Tracer.trace("Main", "Load extensions") {
            val rules = getRules(
                analysisMode = analysisMode,
                ruleSetProviders = ruleSetsProvider.invoke(),
                config = settings.config,
                log = settings::debug,
            )
            processorsProvider.invoke() to rules
        }

        val result = Tracer.trace("Main", "Analyzer") {
            val analyzer = Analyzer(settings, rules.filter { it.ruleInstance.active }, processors, analysisMode)
            Tracer.trace("Main", "Processors onStart") {
                processors.forEach { Tracer.trace("Main", it.id) { it.onStart(filesToAnalyze) } }
            }
            val issues = analyzer.run(filesToAnalyze)
            val detektion = Detektion(issues, rules.map { it.ruleInstance })
            Tracer.trace("Main", "Processors onFinish") {
                processors.fold(detektion) { acc, processor ->
                    Tracer.trace("Main", processor.id) { processor.onFinish(filesToAnalyze, acc) }
                }
            }
        }

        return Tracer.trace("Main", "Reporting") { handleReportingExtensions(settings, result) }
    }

    private fun validateClasspath(files: List<KtFile>) {
        val collector = DetektMessageCollector(
            minSeverity = CompilerMessageSeverity.ERROR,
            debugPrinter = settings::debug,
            warningPrinter = settings::info,
            isDebugEnabled = settings.spec.loggingSpec.debug
        )

        files.forEach { file: KtFile ->
            analyze(file) {
                file.collectDiagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS).forEach { diagnostic ->
                    val lineAndColumnRange =
                        getLineAndColumnRangeInPsiFile(diagnostic.psi.containingFile, diagnostic.psi.textRange)

                    val location = CompilerMessageLocationWithRange.create(
                        diagnostic.psi.containingFile.virtualFile.path,
                        lineAndColumnRange.start.line,
                        lineAndColumnRange.start.column,
                        lineAndColumnRange.end.line,
                        lineAndColumnRange.end.column,
                        lineAndColumnRange.start.lineContent
                    )
                    collector.report(
                        diagnostic.severity.toCompilerMessageSeverity(),
                        diagnostic.defaultMessage,
                        location,
                    )
                }
            }
        }

        collector.printIssuesCountIfAny()
    }
}

private fun KaSeverity.toCompilerMessageSeverity(): CompilerMessageSeverity =
    when (this) {
        KaSeverity.ERROR -> CompilerMessageSeverity.ERROR
        KaSeverity.WARNING -> CompilerMessageSeverity.WARNING
        KaSeverity.INFO -> CompilerMessageSeverity.INFO
    }
