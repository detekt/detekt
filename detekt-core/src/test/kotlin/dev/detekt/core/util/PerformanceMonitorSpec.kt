package dev.detekt.core.util

import dev.detekt.core.tooling.DefaultDetektProvider
import dev.detekt.core.util.PerformanceMonitor.Phase.ValidateClasspath
import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.spec.ProcessingSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PerformanceMonitorSpec {

    @Test
    fun `all phases have a measurement`() {
        val actual = runDetekt(validateClasspath = true)

        assertThat(actual).contains(PerformanceMonitor.Phase.entries.map { it.name })
    }

    @Test
    fun `all phases have a measurement except ValidateClasspath when validateClasspath is disabled`() {
        val actual = runDetekt(validateClasspath = false)

        assertThat(actual)
            .contains(PerformanceMonitor.Phase.entries.minus(ValidateClasspath).map { it.name })
            .doesNotContain(ValidateClasspath.name)
    }

    private fun runDetekt(validateClasspath: Boolean): String {
        val output = StringPrintStream()
        val spec = ProcessingSpec {
            project {
                basePath = resourceAsPath("")
                inputPaths = listOf(resourceAsPath("cases/Test.kt"))
                this.validateClasspath = validateClasspath
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
