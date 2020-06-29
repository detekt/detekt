package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.Detekt
import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.MaxIssuesReached
import io.github.detekt.tooling.api.UnexpectedError
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.config.getOrComputeWeightedAmountOfIssues
import io.gitlab.arturbosch.detekt.core.config.isValidAndSmallerOrEqual
import io.gitlab.arturbosch.detekt.core.config.maxIssues
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import java.nio.file.Path
import java.nio.file.Paths

class AnalysisFacade(
    private val spec: ProcessingSpec,
) : Detekt {

    override fun run(): AnalysisResult = runAnalysis { DefaultLifecycle(spec, it) }

    override fun run(path: Path): AnalysisResult =
        runAnalysis { DefaultLifecycle(spec, it, pathToKtFile(path)) }

    override fun run(sourceCode: String, filename: String): AnalysisResult =
        runAnalysis { DefaultLifecycle(spec, it, contentToKtFile(sourceCode, Paths.get(filename))) }

    override fun run(files: Collection<KtFile>, bindingContext: BindingContext): AnalysisResult =
        runAnalysis {
            DefaultLifecycle(
                spec,
                it,
                parsingStrategy = { _, _ -> files.toList() },
                bindingProvider = { bindingContext }
            )
        }

    override fun run(files: Collection<KtFile>, bindingTrace: BindingTrace): AnalysisResult =
        run(files, bindingTrace.bindingContext)

    private fun runAnalysis(createLifecycle: (ProcessingSettings) -> Lifecycle): AnalysisResult = spec.withSettings {
        val result = runCatching { createLifecycle(this).analyze() }
        when (val error = result.exceptionOrNull()) {
            is InvalidConfig -> DefaultAnalysisResult(null, error)
            is Throwable -> DefaultAnalysisResult(null, UnexpectedError(error))
            else -> {
                val container = result.getOrElse { DetektResult(emptyMap()) }
                if (spec.baselineSpec.shouldCreateDuringAnalysis) {
                    DefaultAnalysisResult(container, null)
                } else {
                    DefaultAnalysisResult(container, checkMaxIssuesReached(container, config))
                }
            }
        }
    }

    private fun checkMaxIssuesReached(result: Detektion, config: Config): MaxIssuesReached? {
        val amount = result.getOrComputeWeightedAmountOfIssues(config)
        val maxIssues = config.maxIssues()
        if (maxIssues.isValidAndSmallerOrEqual(amount)) {
            return MaxIssuesReached("Build failed with $amount weighted issues (threshold defined was $maxIssues).")
        }
        return null
    }
}
