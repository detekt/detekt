package dev.detekt.core

import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.compileContentForTest
import dev.detekt.tooling.api.AnalysisMode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ParallelAnalysisSpec {

    @Nested
    inner class `parallel analysis warning in full mode` {

        @Test
        fun `warns when full analysis is run with parallel analysis enabled`() {
            val output = StringPrintStream()
            val settings = createProcessingSettings(outputChannel = output) {
                execution {
                    parallelAnalysis = true
                }
            }
            val analyzer = Analyzer(
                settings = settings,
                rules = emptyList(),
                processors = emptyList(),
                analysisMode = AnalysisMode.Full,
            )

            settings.use {
                analyzer.run(listOf(compileContentForTest("class A")))
            }

            assertThat(output.toString()).contains(PARALLEL_FULL_ANALYSIS_WARNING)
            assertThat(output.toString()).contains("https://youtrack.jetbrains.com/issue/KT-88461")
        }

        @Test
        fun `does not warn when full analysis is run with parallel analysis disabled`() {
            val output = StringPrintStream()
            val settings = createProcessingSettings(outputChannel = output) {
                execution {
                    parallelAnalysis = false
                }
            }
            val analyzer = Analyzer(
                settings = settings,
                rules = emptyList(),
                processors = emptyList(),
                analysisMode = AnalysisMode.Full,
            )

            settings.use {
                analyzer.run(listOf(compileContentForTest("class A")))
            }

            assertThat(output.toString()).doesNotContain("KT-88461")
        }

        @Test
        fun `does not warn when light analysis is run with parallel analysis enabled`() {
            val output = StringPrintStream()
            val settings = createProcessingSettings(outputChannel = output) {
                execution {
                    parallelAnalysis = true
                }
            }
            val analyzer = Analyzer(
                settings = settings,
                rules = emptyList(),
                processors = emptyList(),
                analysisMode = AnalysisMode.Light,
            )

            settings.use {
                analyzer.run(listOf(compileContentForTest("class A")))
            }

            assertThat(output.toString()).doesNotContain("KT-88461")
        }

        @Test
        fun `does not duplicate warning when run multiple times on same settings`() {
            val output = StringPrintStream()
            val settings = createProcessingSettings(outputChannel = output) {
                execution {
                    parallelAnalysis = true
                }
            }
            val analyzer = Analyzer(
                settings = settings,
                rules = emptyList(),
                processors = emptyList(),
                analysisMode = AnalysisMode.Full,
            )

            settings.use {
                analyzer.run(listOf(compileContentForTest("class A")))
                analyzer.run(listOf(compileContentForTest("class B")))
            }

            val warningCount = output.toString().lines().count { it.contains("KT-88461") }
            assertThat(warningCount).isEqualTo(1)
        }
    }

    @Nested
    inner class `preservation of user-configured parallelism` {

        @Test
        fun `full analysis dispatches to executor when parallelAnalysis is true`() {
            val executor = CountingExecutor()
            val settings = createProcessingSettings {
                execution {
                    parallelAnalysis = true
                    executorService = executor
                }
            }
            val analyzer = Analyzer(
                settings = settings,
                rules = emptyList(),
                processors = emptyList(),
                analysisMode = AnalysisMode.Full,
            )

            settings.use {
                analyzer.run(listOf(compileContentForTest("class A"), compileContentForTest("class B")))
            }

            assertThat(executor.submittedTasks.get()).isEqualTo(2)
        }

        @Test
        fun `full analysis runs synchronously when parallelAnalysis is false`() {
            val executor = CountingExecutor()
            val settings = createProcessingSettings {
                execution {
                    parallelAnalysis = false
                    executorService = executor
                }
            }
            val analyzer = Analyzer(
                settings = settings,
                rules = emptyList(),
                processors = emptyList(),
                analysisMode = AnalysisMode.Full,
            )

            settings.use {
                analyzer.run(listOf(compileContentForTest("class A"), compileContentForTest("class B")))
            }

            assertThat(executor.submittedTasks.get()).isEqualTo(0)
        }

        @Test
        fun `light analysis dispatches to executor when parallelAnalysis is true`() {
            val executor = CountingExecutor()
            val settings = createProcessingSettings {
                execution {
                    parallelAnalysis = true
                    executorService = executor
                }
            }
            val analyzer = Analyzer(
                settings = settings,
                rules = emptyList(),
                processors = emptyList(),
                analysisMode = AnalysisMode.Light,
            )

            settings.use {
                analyzer.run(listOf(compileContentForTest("class A"), compileContentForTest("class B")))
            }

            assertThat(executor.submittedTasks.get()).isEqualTo(2)
        }
    }
}

private class CountingExecutor : AbstractExecutorService() {
    val submittedTasks = AtomicInteger(0)

    override fun execute(command: Runnable) {
        submittedTasks.incrementAndGet()
        command.run()
    }

    override fun shutdown() = Unit
    override fun shutdownNow(): MutableList<Runnable> = mutableListOf()
    override fun isShutdown(): Boolean = true
    override fun isTerminated(): Boolean = true
    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = true
}
