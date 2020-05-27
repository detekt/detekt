package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.PlainTextMessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory

class DetektFacade(
    private val detektor: Detektor,
    settings: ProcessingSettings,
    private val processors: List<FileProcessListener>
) {

    private val saveSupported = settings.autoCorrect
    private val inputPaths = settings.inputPaths
    private val classpath = settings.classpath
    private val environment = settings.environment
    private val compiler = KtTreeCompiler.instance(settings)

    fun run(): Detektion {
        val filesToAnalyze = inputPaths.flatMap(compiler::compile)
        val bindingContext = generateBindingContext(filesToAnalyze)

        processors.forEach { it.onStart(filesToAnalyze) }

        val findings = detektor.run(filesToAnalyze, bindingContext)

        val result = DetektResult(findings.toSortedMap())

        if (saveSupported) {
            KtFileModifier().saveModifiedFiles(filesToAnalyze) { result.add(it) }
        }

        processors.forEach { it.onFinish(filesToAnalyze, result) }
        return result
    }

    private fun generateBindingContext(filesForEnvironment: List<KtFile>): BindingContext {
        return if (classpath.isNotEmpty()) {
            val analyzer = AnalyzerWithCompilerReport(
                PrintingMessageCollector(System.err, DetektMessageRenderer, true),
                environment.configuration.languageVersionSettings
            )
            analyzer.analyzeAndReport(filesForEnvironment) {
                TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
                    environment.project, filesForEnvironment, NoScopeRecordCliBindingTrace(),
                    environment.configuration, environment::createPackagePartProvider,
                    ::FileBasedDeclarationProviderFactory
                )
            }
            analyzer.analysisResult.bindingContext
        } else {
            BindingContext.EMPTY
        }
    }

    private object DetektMessageRenderer : PlainTextMessageRenderer() {
        override fun getName() = "detekt message renderer"
        override fun getPath(location: CompilerMessageLocation) = location.path
        override fun render(
            severity: CompilerMessageSeverity,
            message: String,
            location: CompilerMessageLocation?
        ): String {
            if (!severity.isError) return ""
            return super.render(severity, message, location)
        }
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
