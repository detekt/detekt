package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.absolutePath
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.util.concurrent.ExecutorService

/**
 * @author Artur Bosch
 */
class Detektor(
    settings: ProcessingSettings,
    private val providers: List<RuleSetProvider>,
    private val processors: List<FileProcessListener> = emptyList()
) {

    private val config: Config = settings.config
    private val executor: ExecutorService = settings.executorService
    private val logger = settings.errorPrinter

    fun run(
        ktFiles: Collection<KtFile>,
        bindingContext: BindingContext = BindingContext.EMPTY
    ): Map<RuleSetId, List<Finding>> = withExecutor(executor) {
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

    private fun KtFile.analyze(bindingContext: BindingContext): Map<RuleSetId, List<Finding>> =
        providers.asSequence()
            .mapNotNull { it.buildRuleset(config) }
            .sortedBy { it.id }
            .distinctBy { it.id }
            .toList()
            .map { ruleSet -> ruleSet.id to ruleSet.accept(this, bindingContext) }
            .toMergedMap()
}
