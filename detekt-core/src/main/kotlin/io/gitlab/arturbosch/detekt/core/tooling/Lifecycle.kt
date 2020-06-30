package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.core.Analyzer
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.FileProcessorLocator
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.config.checkConfiguration
import io.gitlab.arturbosch.detekt.core.extensions.handleReportingExtensions
import io.gitlab.arturbosch.detekt.core.generateBindingContext
import io.gitlab.arturbosch.detekt.core.reporting.OutputFacade
import io.gitlab.arturbosch.detekt.core.rules.createRuleProviders
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

internal interface Lifecycle {

    val spec: ProcessingSpec
    val settings: ProcessingSettings
    val parsingStrategy: ParsingStrategy
    val bindingProvider: (files: List<KtFile>) -> BindingContext
    val processorsProvider: () -> List<FileProcessListener>
    val ruleSetsProvider: () -> List<RuleSetProvider>

    fun analyze(): Detektion {
        checkConfiguration(settings)
        val filesToAnalyze = parsingStrategy.invoke(spec, settings)
        val bindingContext = bindingProvider.invoke(filesToAnalyze)
        val processors = processorsProvider.invoke()
        val ruleSets = ruleSetsProvider.invoke()

        val detektor = Analyzer(settings, ruleSets, processors)

        processors.forEach { it.onStart(filesToAnalyze) }
        val findings: Map<RuleSetId, List<Finding>> = detektor.run(filesToAnalyze, bindingContext)
        var result: Detektion = DetektResult(findings.toSortedMap())
        processors.forEach { it.onFinish(filesToAnalyze, result) }

        result = handleReportingExtensions(settings, result)
        OutputFacade(settings).run(result)
        return result
    }
}

internal class DefaultLifecycle(
    override val spec: ProcessingSpec,
    override val settings: ProcessingSettings,
    override val parsingStrategy: ParsingStrategy = inputPathsToKtFiles,
    override val bindingProvider: (files: List<KtFile>) -> BindingContext =
        { generateBindingContext(settings.environment, settings.classpath, it) },
    override val processorsProvider: () -> List<FileProcessListener> =
        { FileProcessorLocator(settings).load() },
    override val ruleSetsProvider: () -> List<RuleSetProvider> =
        { spec.rulesSpec.runPolicy.createRuleProviders(settings) },
) : Lifecycle
