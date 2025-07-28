package io.gitlab.arturbosch.detekt.core.tooling

import dev.detekt.tooling.api.AnalysisResult
import dev.detekt.tooling.api.Detekt
import dev.detekt.tooling.api.DetektError
import dev.detekt.tooling.api.InvalidConfig
import dev.detekt.tooling.api.IssuesFound
import dev.detekt.tooling.api.UnexpectedError
import dev.detekt.tooling.api.spec.ProcessingSpec
import dev.detekt.tooling.internal.DefaultAnalysisResult
import dev.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.config.check
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class AnalysisFacade(
    private val spec: ProcessingSpec,
) : Detekt {

    override fun run(): AnalysisResult = runAnalysis {
        DefaultLifecycle(spec.getDefaultConfiguration(), it)
    }

    override fun run(files: Collection<KtFile>, bindingContext: BindingContext): AnalysisResult =
        runAnalysis {
            DefaultLifecycle(
                spec.getDefaultConfiguration(),
                it,
                bindingProvider = { bindingContext }
            )
        }

    internal fun runAnalysis(createLifecycle: (ProcessingSettings) -> Lifecycle): AnalysisResult = spec.withSettings {
        val result = runCatching { createLifecycle(this).analyze() }
        when (val error = result.exceptionOrNull()) {
            null -> {
                val container = checkNotNull(result.getOrNull()) { "Result should not be null at this point." }
                DefaultAnalysisResult(container, checkFailurePolicy(container))
            }

            is InvalidConfig -> DefaultAnalysisResult(null, error)
            else -> DefaultAnalysisResult(null, UnexpectedError(error))
        }
    }

    private fun checkFailurePolicy(result: Detektion): DetektError? {
        if (spec.baselineSpec.shouldCreateDuringAnalysis) {
            return null // never fail the build as on next run all current issues are suppressed via the baseline
        }

        val error = runCatching {
            spec.rulesSpec.failurePolicy.check(result)
        }.exceptionOrNull()

        return when {
            error is IssuesFound -> error
            error != null -> UnexpectedError(error)
            else -> null
        }
    }
}
