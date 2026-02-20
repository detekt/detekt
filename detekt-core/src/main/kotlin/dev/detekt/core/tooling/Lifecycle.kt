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
import dev.detekt.core.rules.createRuleProviders
import dev.detekt.core.util.PerformanceMonitor.Phase
import dev.detekt.parser.DetektMessageCollector
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
    val bindingProvider: (files: List<KtFile>) -> Unit =
        {
            val collector = DetektMessageCollector(
                minSeverity = CompilerMessageSeverity.ERROR,
                debugPrinter = settings::debug,
                warningPrinter = settings::info,
                isDebugEnabled = settings.spec.loggingSpec.debug
            )

            it.forEach { file: KtFile ->
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
        },
    val processorsProvider: () -> List<FileProcessListener> =
        { FileProcessorLocator(settings).load() },
    val ruleSetsProvider: () -> List<RuleSetProvider> =
        { settings.createRuleProviders() },
) {

    private fun <R> measure(phase: Phase, block: () -> R): R = settings.monitor.measure(phase, block)

    fun analyze(): Detektion {
        measure(Phase.ValidateConfig) { checkConfiguration(settings, baselineConfig) }
        val filesToAnalyze = measure(Phase.Parsing) { settings.ktFiles }
        if (settings.spec.projectSpec.analysisMode == AnalysisMode.full) {
            measure(Phase.Binding) { bindingProvider.invoke(filesToAnalyze) }
        }
        val analysisMode = settings.spec.projectSpec.analysisMode
        val (processors, rules) = measure(Phase.LoadingExtensions) {
            val rules = getRules(
                analysisMode = analysisMode,
                ruleSetProviders = ruleSetsProvider.invoke(),
                config = settings.config,
                log = settings::debug,
            )
            processorsProvider.invoke() to rules
        }

        val result = measure(Phase.Analyzer) {
            val analyzer = Analyzer(settings, rules.filter { it.ruleInstance.active }, processors, analysisMode)
            processors.forEach { it.onStart(filesToAnalyze) }
            val issues = analyzer.run(filesToAnalyze)
            val detektion = Detektion(issues, rules.map { it.ruleInstance })
            processors.fold(detektion) { acc, processor -> processor.onFinish(filesToAnalyze, acc) }
        }

        return measure(Phase.Reporting) { handleReportingExtensions(settings, result) }
    }
}

private fun KaSeverity.toCompilerMessageSeverity(): CompilerMessageSeverity =
    when (this) {
        KaSeverity.ERROR -> CompilerMessageSeverity.ERROR
        KaSeverity.WARNING -> CompilerMessageSeverity.WARNING
        KaSeverity.INFO -> CompilerMessageSeverity.INFO
    }
