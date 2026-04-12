package dev.detekt.core.util

import dev.detekt.core.tooling.DefaultDetektProvider
import dev.detekt.core.util.PerformanceMonitor.Phase.ValidateClasspath
import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.AnalysisMode
import dev.detekt.tooling.api.spec.ProcessingSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PerformanceMonitorSpec {

    @Test
    fun `all phases have a measurement`() {
        val actual = StringPrintStream()
        val spec = ProcessingSpec {
            project {
                basePath = resourceAsPath("")
                inputPaths = listOf(resourceAsPath("cases/Test.kt"))
                analysisMode = AnalysisMode.full
            }
            logging {
                debug = true
                outputChannel = actual
            }
        }

        DefaultDetektProvider().get(spec).run()

        assertThat(actual.toString()).contains(PerformanceMonitor.Phase.entries.map { it.name })
    }

    @Test
    fun `all phases have a measurement except binding on light`() {
        val actual = StringPrintStream()
        val spec = ProcessingSpec {
            project {
                basePath = resourceAsPath("")
                inputPaths = listOf(resourceAsPath("cases/Test.kt"))
                analysisMode = AnalysisMode.light
            }
            logging {
                debug = true
                outputChannel = actual
            }
        }

        DefaultDetektProvider().get(spec).run()

        assertThat(actual.toString())
            .contains(PerformanceMonitor.Phase.entries.minus(ValidateClasspath).map { it.name })
            .doesNotContain(ValidateClasspath.name)
    }
}
