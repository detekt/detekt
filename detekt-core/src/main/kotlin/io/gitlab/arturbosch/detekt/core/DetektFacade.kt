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
    private val detektor: Detektor,
    private val settings: ProcessingSettings,
    private val processors: List<FileProcessListener>
) {

    private val saveSupported = settings.autoCorrect
    private val inputPaths = settings.inputPaths
    private val compiler = KtTreeCompiler.instance(settings)

    fun run(): Detektion {
        val (checkConfigTime) = measure { checkConfiguration(settings) }
        settings.debug { "Checking config took $checkConfigTime ms" }

        val filesToAnalyze = inputPaths.flatMap(compiler::compile)
        val bindingContext = generateBindingContext(settings.environment, settings.classpath, filesToAnalyze)

        processors.forEach { it.onStart(filesToAnalyze) }

        val findings: Map<RuleSetId, List<Finding>> = detektor.run(filesToAnalyze, bindingContext)
        var result: Detektion = DetektResult(findings.toSortedMap())

        if (saveSupported) {
            KtFileModifier().saveModifiedFiles(filesToAnalyze) { result.add(it) }
        }

        processors.forEach { it.onFinish(filesToAnalyze, result) }

        result = handleReportingExtensions(settings, result)
        OutputFacade(settings).run(result)
        return result
    }

    companion object {

        fun create(settings: ProcessingSettings): DetektFacade {
            val providers = RuleSetLocator(settings).load()
            val processors = FileProcessorLocator(settings).load()
            return create(settings, providers, processors)
        }

        fun create(settings: ProcessingSettings, vararg providers: RuleSetProvider): DetektFacade {
            return create(settings, providers.toList(), emptyList())
        }

        fun create(settings: ProcessingSettings, vararg processors: FileProcessListener): DetektFacade {
            return create(settings, emptyList(), processors.toList())
        }

        fun create(
            settings: ProcessingSettings,
            providers: List<RuleSetProvider>,
            processors: List<FileProcessListener>
        ): DetektFacade {
            return DetektFacade(Detektor(settings, providers, processors), settings, processors)
        }
    }
}
