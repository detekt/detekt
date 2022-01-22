package io.gitlab.arturbosch.detekt.core.util

import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.core.tooling.DefaultDetektProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PerformanceMonitorSpec {

    @Test
    fun `all phases have a measurement`() {
        val actual = StringPrintStream()
        val spec = ProcessingSpec {
            project {
                inputPaths = listOf(resourceAsPath("cases/Test.kt"))
            }
            logging {
                debug = true
                outputChannel = actual
            }
        }

        DefaultDetektProvider().get(spec).run()

        assertThat(actual.toString()).contains(PerformanceMonitor.Phase.values().map { it.name })
    }
}
