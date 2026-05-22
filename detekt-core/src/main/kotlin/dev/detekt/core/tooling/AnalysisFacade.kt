@file:Suppress("StringLiteralDuplication")

package dev.detekt.core.tooling

import androidx.tracing.Counter
import androidx.tracing.DelicateTracingApi
import androidx.tracing.EventMetadataCloseable
import androidx.tracing.ExperimentalContextPropagation
import androidx.tracing.PropagationToken
import androidx.tracing.Tracer
import androidx.tracing.wire.TraceDriver
import androidx.tracing.wire.TraceSink
import dev.detekt.api.Detektion
import dev.detekt.core.ProcessingSettings
import dev.detekt.core.config.FailurePolicyResult
import dev.detekt.core.config.check
import dev.detekt.core.reporting.OutputFacade
import dev.detekt.core.reporting.OutputFacade.ReportPaths.Hidden
import dev.detekt.core.reporting.OutputFacade.ReportPaths.Show
import dev.detekt.tooling.api.AnalysisResult
import dev.detekt.tooling.api.Detekt
import dev.detekt.tooling.api.DetektError
import dev.detekt.tooling.api.InvalidConfig
import dev.detekt.tooling.api.IssuesFound
import dev.detekt.tooling.api.UnexpectedError
import dev.detekt.tooling.api.spec.ProcessingSpec
import dev.detekt.tooling.internal.DefaultAnalysisResult
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

class AnalysisFacade(private val spec: ProcessingSpec) : Detekt {

    override fun run(): AnalysisResult = runAnalysis { Lifecycle(spec.getDefaultConfiguration(), it) }

    override fun run(files: Collection<KtFile>): AnalysisResult =
        runAnalysis { Lifecycle(spec.getDefaultConfiguration(), it) }

    internal fun runAnalysis(createLifecycle: (ProcessingSettings) -> Lifecycle): AnalysisResult =
        createTraceDriver().use {
            Tracer = it.tracer
            spec.withSettings {
                runCatching {
                    val lifecycle = Tracer.trace("Main", "Create Lifecycle") { createLifecycle(this) }
                    Tracer.trace("Main", "Analyze") { lifecycle.analyze() }
                }.fold(
                    onSuccess = { detektion ->
                        val fail = checkFailurePolicy(detektion)
                        @Suppress("TooGenericExceptionCaught")
                        try {
                            OutputFacade(this).run(detektion, if (fail != null) Show else Hidden)
                        } catch (ex: Exception) {
                            DefaultAnalysisResult(detektion, UnexpectedError(ex))
                        }
                        DefaultAnalysisResult(detektion, fail)
                    },
                    onFailure = { error ->
                        DefaultAnalysisResult(null, if (error is InvalidConfig) error else UnexpectedError(error))
                    },
                )
            }
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

fun createSink(outputDirectory: File) = TraceSink(outputDirectory, sequenceId = 1)

fun createTraceDriver(): TraceDriver = TraceDriver(createSink(File(".")), isEnabled = true)

fun main() {
    val driver = createTraceDriver()
    driver.use {
        driver.tracer.trace(category = "MAIN", name = "basic") {
            Thread.sleep(100L)
        }
    }
}

@Suppress("NotImplementedDeclaration", "PropertyName")
var Tracer: Tracer = object : Tracer(false) {
    @ExperimentalContextPropagation
    override fun tokenForManualPropagation(): PropagationToken {
        TODO("Not yet implemented")
    }

    @DelicateTracingApi
    override fun tokenFromThreadContext(): PropagationToken {
        TODO("Not yet implemented")
    }

    @DelicateTracingApi
    override suspend fun tokenFromCoroutineContext(): PropagationToken {
        TODO("Not yet implemented")
    }

    @DelicateTracingApi
    override fun beginSectionWithMetadata(
        category: String,
        name: String,
        token: PropagationToken?,
        isRoot: Boolean,
    ): EventMetadataCloseable {
        TODO("Not yet implemented")
    }

    @DelicateTracingApi
    override suspend fun beginCoroutineSectionWithMetadata(
        category: String,
        name: String,
        token: PropagationToken?,
        isRoot: Boolean,
    ): EventMetadataCloseable {
        TODO("Not yet implemented")
    }

    override fun counter(category: String, name: String): Counter {
        TODO("Not yet implemented")
    }

    @DelicateTracingApi
    override fun instant(category: String, name: String): EventMetadataCloseable {
        TODO("Not yet implemented")
    }
}
