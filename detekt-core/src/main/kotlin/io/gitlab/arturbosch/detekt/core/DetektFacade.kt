package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.core.config.checkConfiguration
import io.gitlab.arturbosch.detekt.core.extensions.handleReportingExtensions
import io.gitlab.arturbosch.detekt.core.reporting.OutputFacade

class DetektFacade(
    private val settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val processors: List<FileProcessListener>,
) {

    private val inputPaths = settings.inputPaths
    private val compiler = KtTreeCompiler.instance(settings)

    fun run(): Detektion {
        // TODO use a monitor class to measure time
        val (engineRunTime, result) = measure {
            val (checkConfigTime) = measure { checkConfiguration(settings) }
            settings.debug { "Checking config took $checkConfigTime ms" }

            val filesToAnalyze = inputPaths.flatMap(compiler::compile)
            val bindingContext = generateBindingContext(settings.environment, settings.classpath, filesToAnalyze)

            processors.forEach { it.onStart(filesToAnalyze) }

            val detektor = Analyzer(settings, providers, processors)
            val findings: Map<RuleSetId, List<Finding>> = detektor.run(filesToAnalyze, bindingContext)
            var result: Detektion = DetektResult(findings.toSortedMap())

            processors.forEach { it.onFinish(filesToAnalyze, result) }

            result = handleReportingExtensions(settings, result)
            OutputFacade(settings).run(result)
            result
        }
        settings.debug { "Running core engine took $engineRunTime ms" }
        return result
    }

    companion object {

        fun create(settings: ProcessingSettings): DetektFacade {
            val (serviceLoadingTime, facade) = measure {
                val providers = RuleSetLocator(settings).load()
                val processors = FileProcessorLocator(settings).load()
                DetektFacade(settings, providers, processors)
            }
            settings.debug { "Loading services took $serviceLoadingTime ms" }
            return facade
        }
    }
}
