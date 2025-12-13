package dev.detekt.core.tooling

import dev.detekt.api.Detektion
import dev.detekt.core.ProcessingSettings
import dev.detekt.core.config.FailurePolicyResult
import dev.detekt.core.config.check
import dev.detekt.tooling.api.AnalysisResult
import dev.detekt.tooling.api.Detekt
import dev.detekt.tooling.api.DetektError
import dev.detekt.tooling.api.InvalidConfig
import dev.detekt.tooling.api.IssuesFound
import dev.detekt.tooling.api.UnexpectedError
import dev.detekt.tooling.api.spec.ProcessingSpec
import dev.detekt.tooling.internal.DefaultAnalysisResult
import org.jetbrains.kotlin.psi.KtFile

class AnalysisFacade(
    private val spec: ProcessingSpec,
) : Detekt {

    override fun run(): AnalysisResult =
        runAnalysis {
            DefaultLifecycle(spec.getDefaultConfiguration(), it)
        }

    override fun run(files: Collection<KtFile>): AnalysisResult =
        runAnalysis {
            DefaultLifecycle(spec.getDefaultConfiguration(), it)
        }

    internal fun runAnalysis(createLifecycle: (ProcessingSettings) -> Lifecycle): AnalysisResult =
        spec.withSettings {
            runCatching { createLifecycle(this).analyze() }.fold(
                onSuccess = { detektion -> DefaultAnalysisResult(detektion, checkFailurePolicy(detektion)) },
                onFailure = { error ->
                    DefaultAnalysisResult(null, if (error is InvalidConfig) error else UnexpectedError(error))
                },
            )
        }

    private fun checkFailurePolicy(detektion: Detektion): DetektError? {
        if (spec.baselineSpec.shouldCreateDuringAnalysis) {
            return null // never fail the build as on next run all current issues are suppressed via the baseline
        }

        return when (val result = spec.rulesSpec.failurePolicy.check(detektion)) {
            is FailurePolicyResult.Fail -> IssuesFound(result.message)
            FailurePolicyResult.Ok -> null
        }
    }
}
