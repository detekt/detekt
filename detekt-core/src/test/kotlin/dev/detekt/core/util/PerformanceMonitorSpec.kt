package dev.detekt.core.util

import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.spec.ProcessingSpec
import dev.detekt.core.tooling.DefaultDetektProvider
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
            }
            logging {
                debug = true
                outputChannel = actual
            }
        }

        DefaultDetektProvider().get(spec).run()

        assertThat(actual.toString()).contains(PerformanceMonitor.Phase.entries.map { it.name })
    }
}
