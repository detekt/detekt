package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.Detekt
import io.github.detekt.tooling.api.DetektError
import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.MaxIssuesReached
import io.github.detekt.tooling.api.UnexpectedError
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.internal.DefaultAnalysisResult
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.config.MaxIssueCheck
import io.gitlab.arturbosch.detekt.core.config.getOrComputeWeightedAmountOfIssues
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Path
import java.nio.file.Paths

class AnalysisFacade(
    private val spec: ProcessingSpec
) : Detekt {

    override fun run(): AnalysisResult = runAnalysis { DefaultLifecycle(spec, it, inputPathsToKtFiles) }

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

    internal fun runAnalysis(createLifecycle: (ProcessingSettings) -> Lifecycle): AnalysisResult = spec.withSettings {
        val result = runCatching { createLifecycle(this).analyze() }
        when (val error = result.exceptionOrNull()) {
            is InvalidConfig -> DefaultAnalysisResult(null, error)
            is Throwable -> DefaultAnalysisResult(null, UnexpectedError(error))
            else -> {
                val container = result.getOrElse { DetektResult(emptyMap()) }
                DefaultAnalysisResult(container, checkMaxIssuesReachedReturningErrors(container, config))
            }
        }
    }

    private fun checkMaxIssuesReachedReturningErrors(result: Detektion, config: Config): DetektError? {
        if (spec.baselineSpec.shouldCreateDuringAnalysis) {
            return null // never fail the build as on next run all current issues are suppressed via the baseline
        }

        val error = runCatching {
            val amount = result.getOrComputeWeightedAmountOfIssues(config)
            MaxIssueCheck(spec.rulesSpec, config).check(amount)
        }.exceptionOrNull()

        return when {
            error is MaxIssuesReached -> error
            error != null -> UnexpectedError(error)
            else -> null
        }
    }
}
