package dev.detekt.core.tooling

import dev.detekt.parser.DetektMessageCollector
import dev.detekt.parser.generateBindingContext
import dev.detekt.tooling.api.AnalysisMode
import dev.detekt.api.Config
import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.RuleSetProvider
import dev.detekt.core.Analyzer
import dev.detekt.core.DetektResult
import dev.detekt.core.FileProcessorLocator
import dev.detekt.core.ProcessingSettings
import dev.detekt.core.config.validation.checkConfiguration
import dev.detekt.core.extensions.handleReportingExtensions
import dev.detekt.core.getRules
import dev.detekt.core.reporting.OutputFacade
import dev.detekt.core.rules.createRuleProviders
import dev.detekt.core.util.PerformanceMonitor.Phase
import dev.detekt.core.util.getOrCreateMonitor
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.diagnostics.KaSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocationWithRange
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils.getLineAndColumnRangeInPsiFile
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

internal interface Lifecycle {

    val baselineConfig: Config
    val settings: ProcessingSettings
    val bindingProvider: (files: List<KtFile>) -> BindingContext
    val processorsProvider: () -> List<FileProcessListener>
    val ruleSetsProvider: () -> List<RuleSetProvider>

    private fun <R> measure(phase: Phase, block: () -> R): R = settings.getOrCreateMonitor().measure(phase, block)

    fun analyze(): Detektion {
        measure(Phase.ValidateConfig) { checkConfiguration(settings, baselineConfig) }
        val filesToAnalyze = measure(Phase.Parsing) { settings.ktFiles }
        val bindingContext = measure(Phase.Binding) { bindingProvider.invoke(filesToAnalyze) }
        val (processors, rules) = measure(Phase.LoadingExtensions) {
            val rules = getRules(
                fullAnalysis = bindingContext != BindingContext.EMPTY,
                ruleSetProviders = ruleSetsProvider.invoke(),
                config = settings.config,
                log = settings::debug,
            )
            processorsProvider.invoke() to rules
        }

        val result = measure(Phase.Analyzer) {
            val analyzer = Analyzer(settings, rules.filter { it.ruleInstance.active }, processors, bindingContext)
            processors.forEach { it.onStart(filesToAnalyze) }
            val issues = analyzer.run(filesToAnalyze)
            val result: Detektion = DetektResult(issues, rules.map { it.ruleInstance })
            processors.forEach { it.onFinish(filesToAnalyze, result) }
            result
        }

        return measure(Phase.Reporting) {
            val finalResult = handleReportingExtensions(settings, result)
            OutputFacade(settings).run(finalResult)
            finalResult
        }
    }
}

internal class DefaultLifecycle(
    override val baselineConfig: Config,
    override val settings: ProcessingSettings,
    override val bindingProvider: (files: List<KtFile>) -> BindingContext =
        {
            if (settings.spec.projectSpec.analysisMode == AnalysisMode.full) {
                val collector = DetektMessageCollector(CompilerMessageSeverity.ERROR, settings::debug, settings::info)

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
                                location
                            )
                        }
                    }
                }

                collector.printIssuesCountIfAny(k2Mode = true)

                generateBindingContext(
                    settings.project,
                    settings.configuration,
                    it,
                    settings::debug,
                    settings::info
                )
            } else {
                BindingContext.EMPTY
            }
        },
    override val processorsProvider: () -> List<FileProcessListener> =
        { FileProcessorLocator(settings).load() },
    override val ruleSetsProvider: () -> List<RuleSetProvider> =
        { settings.createRuleProviders() },
) : Lifecycle

private fun KaSeverity.toCompilerMessageSeverity(): CompilerMessageSeverity =
    when (this) {
        KaSeverity.ERROR -> CompilerMessageSeverity.ERROR
        KaSeverity.WARNING -> CompilerMessageSeverity.WARNING
        KaSeverity.INFO -> CompilerMessageSeverity.INFO
    }
