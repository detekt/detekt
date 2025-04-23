package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.parser.generateBindingContext
import io.github.detekt.tooling.api.AnalysisMode
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.core.Analyzer
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.FileProcessorLocator
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.config.validation.checkConfiguration
import io.gitlab.arturbosch.detekt.core.extensions.handleReportingExtensions
import io.gitlab.arturbosch.detekt.core.getRules
import io.gitlab.arturbosch.detekt.core.reporting.OutputFacade
import io.gitlab.arturbosch.detekt.core.rules.createRuleProviders
import io.gitlab.arturbosch.detekt.core.util.PerformanceMonitor.Phase
import io.gitlab.arturbosch.detekt.core.util.getOrCreateMonitor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

internal interface Lifecycle {

    val baselineConfig: Config
    val settings: ProcessingSettings
    val parsingStrategy: ParsingStrategy
    val bindingProvider: (files: List<KtFile>) -> BindingContext
    val processorsProvider: () -> List<FileProcessListener>
    val ruleSetsProvider: () -> List<RuleSetProvider>

    private fun <R> measure(phase: Phase, block: () -> R): R = settings.getOrCreateMonitor().measure(phase, block)

    fun analyze(): Detektion {
        measure(Phase.ValidateConfig) { checkConfiguration(settings, baselineConfig) }
        val filesToAnalyze = measure(Phase.Parsing) { parsingStrategy.invoke(settings) }
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
    override val parsingStrategy: ParsingStrategy,
    override val bindingProvider: (files: List<KtFile>) -> BindingContext =
        {
            if (settings.spec.projectSpec.analysisMode == AnalysisMode.full) {
                generateBindingContext(
                    settings.environment.project,
                    settings.environment.configuration,
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
