package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.createKotlinCoreEnvironment
import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import java.util.concurrent.ExecutorService

/**
 * @author Artur Bosch
 */
class Detektor(
    settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val processors: List<FileProcessListener> = emptyList()) {

    private val config: Config = settings.config
    private val testPattern: TestPattern = settings.loadTestPattern()
    private val executor: ExecutorService = settings.executorService
    private val logger = settings.errorPrinter

    fun run(
        ktFiles: List<KtFile>,
        environment: KotlinCoreEnvironment = createKotlinCoreEnvironment(),
        resolveTypes: Boolean = false
    ): Map<RuleSetId, List<Finding>> = withExecutor(executor) {
        val bindingContext = if (resolveTypes) {
            val analyzer = AnalyzerWithCompilerReport(
                PrintingMessageCollector(System.out, MessageRenderer.PLAIN_FULL_PATHS, true),
                environment.configuration.languageVersionSettings
            )
            analyzer.analyzeAndReport(ktFiles) {
                TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
                    environment.project, ktFiles, NoScopeRecordCliBindingTrace(),
                    environment.configuration, environment::createPackagePartProvider,
                    ::FileBasedDeclarationProviderFactory
                )
            }
            analyzer.analysisResult.bindingContext
        } else {
            BindingContext.EMPTY
        }

        val futures = ktFiles.map { file ->
            runAsync {
                processors.forEach { it.onProcess(file) }
                file.analyze(bindingContext).apply {
                    processors.forEach { it.onProcessComplete(file, this) }
                }
            }.exceptionally { error ->
                logger.println("Analyzing '${file.absolutePath()}' led to an exception.\n" +
                        "The original exception message was: ${error.localizedMessage}\n" +
                        "Running detekt '${whichDetekt()}' on Java '${whichJava()}' on OS '${whichOS()}'.\n" +
                        "If the exception message does not help, please feel free to create an issue on our github page."
                )
                error.printStacktraceRecursively(logger)
                emptyMap()
            }
        }

        val result = HashMap<RuleSetId, List<Finding>>()
        for (map in awaitAll(futures)) {
            result.mergeSmells(map)
        }

        result
    }

    private fun KtFile.analyze(bindingContext: BindingContext): Map<RuleSetId, List<Finding>> {
        var ruleSets = providers.asSequence()
                .mapNotNull { it.buildRuleset(config) }
                .sortedBy { it.id }
                .distinctBy { it.id }
                .toList()

        return if (testPattern.isTestSource(this)) {
            ruleSets = ruleSets.filterNot { testPattern.matchesRuleSet(it.id) }
            ruleSets.map { ruleSet -> ruleSet.id to ruleSet.accept(this, testPattern.excludingRules, bindingContext) }
        } else {
            ruleSets.map { ruleSet -> ruleSet.id to ruleSet.accept(this, bindingContext) }
        }.toMergedMap()
    }
}
