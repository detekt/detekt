package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.Detekt
import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.MaxIssuesReached
import io.github.detekt.tooling.api.UnexpectedError
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.DetektResult
import io.gitlab.arturbosch.detekt.core.config.getOrComputeWeightedAmountOfIssues
import io.gitlab.arturbosch.detekt.core.config.isValidAndSmallerOrEqual
import io.gitlab.arturbosch.detekt.core.config.maxIssues
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import java.nio.file.Path

@Suppress("detekt.NotImplementedDeclaration")
class DetektFacade(
    private val spec: ProcessingSpec,
) : Detekt {

    override fun run(): AnalysisResult = spec.withSettings {
        val result = runCatching { DetektFacade.create(this).run() }
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

    override fun run(path: Path): AnalysisResult {
        TODO()
    }

    override fun run(sourceCode: String, filename: String): AnalysisResult {
        TODO()
    }

    override fun run(files: Collection<KtFile>, bindingContext: BindingContext): AnalysisResult {
        TODO()
    }

    override fun run(files: Collection<KtFile>, bindingTrace: BindingTrace): AnalysisResult {
        TODO()
    }
}
