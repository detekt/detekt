package dev.detekt.core.util

import dev.detekt.core.tooling.DefaultDetektProvider
import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.AnalysisMode
import dev.detekt.tooling.api.spec.ProcessingSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PerformanceMonitorSpec {

    @Test
    fun `all phases have a measurement`() {
        val actual = runDetekt(analysisMode = AnalysisMode.full, diagnostics = true)

        assertThat(actual).contains(PerformanceMonitor.Phase.entries.map { it.name })
    }

    @Test
    fun `all phases have a measurement except binding on light`() {
        val actual = runDetekt(analysisMode = AnalysisMode.light, diagnostics = true)

        assertThat(actual)
            .contains(PerformanceMonitor.Phase.entries.minus(PerformanceMonitor.Phase.Binding).map { it.name })
            .doesNotContain(PerformanceMonitor.Phase.Binding.name)
    }

    @Test
    fun `all phases have a measurement except binding when diagnostics disabled`() {
        val actual = runDetekt(analysisMode = AnalysisMode.full, diagnostics = false)

        assertThat(actual)
            .contains(PerformanceMonitor.Phase.entries.minus(PerformanceMonitor.Phase.Binding).map { it.name })
            .doesNotContain(PerformanceMonitor.Phase.Binding.name)
    }

    private fun runDetekt(analysisMode: AnalysisMode, diagnostics: Boolean): String {
        val output = StringPrintStream()
        val spec = ProcessingSpec {
            project {
                basePath = resourceAsPath("")
                inputPaths = listOf(resourceAsPath("cases/Test.kt"))
                this.analysisMode = analysisMode
                this.diagnostics = diagnostics
            }
            logging {
                debug = true
                outputChannel = output
            }
        }
        DefaultDetektProvider().get(spec).run()
        return output.toString()
    }
}
