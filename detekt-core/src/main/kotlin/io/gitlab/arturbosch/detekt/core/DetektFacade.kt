package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.createCompilerConfiguration
import io.gitlab.arturbosch.detekt.api.createKotlinCoreEnvironment
import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class DetektFacade(
    private val detektor: Detektor,
    settings: ProcessingSettings,
    private val processors: List<FileProcessListener>
) {

    private val saveSupported = settings.config.valueOrDefault("autoCorrect", false)
    private val inputPaths = settings.inputPaths
    private val classpath = settings.classpath
    private val pathFilters = settings.pathFilters
    private val compilerConfiguration = createCompilerConfiguration(classpath, inputPaths)
    private val environment = createKotlinCoreEnvironment(compilerConfiguration)
    private val compiler = KtTreeCompiler.instance(settings)

    fun run(): Detektion {
        val notifications = mutableListOf<Notification>()
        val findings = HashMap<String, List<Finding>>()

        val filesToAnalyze = environment.getSourceFiles()
            .filter { file -> !pathFilters.any { it.matches(Paths.get(file.virtualFilePath)) } }
            .apply { forEach { it.addUserData(it.virtualFilePath) } }
        val bindingContext = generateBindingContext(filesToAnalyze)

        processors.forEach { it.onStart(filesToAnalyze) }

        if (saveSupported) {
            for (current in inputPaths) {
                val files = compiler.compile(current)

                KtFileModifier(current).saveModifiedFiles(files) {
                    notifications.add(it)
                }
            }
        }

        findings.mergeSmells(detektor.run(filesToAnalyze, bindingContext))

        val result = DetektResult(findings.toSortedMap())
        processors.forEach { it.onFinish(filesToAnalyze, result) }
        return result
    }

    private fun generateBindingContext(filesForEnvironment: List<KtFile>): BindingContext {
        return if (classpath.isNotEmpty()) {
            val analyzer = AnalyzerWithCompilerReport(
                PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, true),
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

    fun run(project: Path, files: List<KtFile>): Detektion = runOnFiles(project, files)

    private fun runOnFiles(current: Path, files: List<KtFile>): DetektResult {
        processors.forEach { it.onStart(files) }

        val findings = detektor.run(files)
        val detektion = DetektResult(findings.toSortedMap())
        if (saveSupported) {
            KtFileModifier(current).saveModifiedFiles(files) {
                detektion.add(it)
            }
        }

        processors.forEach { it.onFinish(files, detektion) }
        return detektion
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
