package dev.detekt.core

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.core.util.PerformanceMonitor
import dev.detekt.test.utils.NullPrintStream
import dev.detekt.test.utils.createEnvironment
import dev.detekt.tooling.api.AnalysisMode
import dev.detekt.tooling.api.spec.ProcessingSpec
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.createFile
import kotlin.io.path.writeText

class ParallelAnalysisSpec {

    @Nested
    inner class `concurrency safety in analysis mode` {

        @Test
        fun `rules in full analysis mode must not execute concurrently on shared session`(@TempDir tempDir: Path) {
            val fileCount = 4
            createFullAnalysisFiles(tempDir, fileCount)

            val activeCount = AtomicInteger(0)
            val maxConcurrent = AtomicInteger(0)

            class ProbeRule(config: Config) : Rule(config, "ProbeRule") {
                override fun visitCallExpression(expression: KtCallExpression) {
                    val count = activeCount.incrementAndGet()
                    maxConcurrent.accumulateAndGet(count, ::maxOf)
                    Thread.sleep(50)
                    analyze(expression) {
                        expression.resolveToCall()?.singleFunctionCallOrNull()?.symbol
                    }
                    activeCount.decrementAndGet()
                    report(Finding(Entity.from(expression), "call"))
                }
            }

            runWithAnalyzer(tempDir, fileCount, AnalysisMode.full, ::ProbeRule)

            // In full analysis mode, rule execution against the shared Analysis API session must be serialized
            assertThat(maxConcurrent.get())
                .withFailMessage(
                    "Expected max concurrent rule executions to be 1 in full analysis mode, but was %d",
                    maxConcurrent.get(),
                )
                .isEqualTo(1)
        }

        @Test
        fun `rules in light analysis mode can execute concurrently in parallel`(@TempDir tempDir: Path) {
            val fileCount = 4
            (1..fileCount).forEach { i ->
                tempDir.resolve("LightTest$i.kt").createFile().writeText("package light\nclass LightTest$i")
            }

            val activeCount = AtomicInteger(0)
            val maxConcurrent = AtomicInteger(0)
            val overlapLatch = CountDownLatch(fileCount)

            class LightProbeRule(config: Config) : Rule(config, "LightProbeRule") {
                override fun visitKtFile(file: KtFile) {
                    val count = activeCount.incrementAndGet()
                    maxConcurrent.accumulateAndGet(count, ::maxOf)
                    overlapLatch.countDown()
                    overlapLatch.await(500, TimeUnit.MILLISECONDS)
                    Thread.sleep(20)
                    activeCount.decrementAndGet()
                    report(Finding(Entity.from(file), "file"))
                }
            }

            runWithAnalyzer(tempDir, fileCount, AnalysisMode.light, ::LightProbeRule)

            // In light analysis mode, rules have no shared Analysis API session constraints and run concurrently
            assertThat(maxConcurrent.get())
                .withFailMessage(
                    "Expected max concurrent rule executions > 1 in light mode, but was %d",
                    maxConcurrent.get(),
                )
                .isGreaterThan(1)
        }
    }
}

private fun createFullAnalysisFiles(tempDir: Path, fileCount: Int) {
    (1..fileCount).forEach { i ->
        tempDir.resolve("Test$i.kt").createFile().writeText(
            """
                package repro
                class Test$i {
                    fun doSomething() {
                        val map = java.util.concurrent.ConcurrentHashMap<String, String>()
                        map.put("k$i", "v$i")
                    }
                }
            """.trimIndent()
        )
    }
}

private fun runWithAnalyzer(tempDir: Path, threadCount: Int, mode: AnalysisMode, ruleProducer: (Config) -> Rule) {
    val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)
    try {
        val spec = ProcessingSpec {
            project {
                basePath = tempDir
                inputPaths = listOf(tempDir)
                analysisMode = mode
            }
            if (mode == AnalysisMode.full) {
                compiler {
                    classpath = createEnvironment().jvmClasspathRoots
                }
            }
            logging {
                outputChannel = NullPrintStream()
                errorChannel = NullPrintStream()
            }
            execution {
                parallelParsing = true
                parallelAnalysis = true
                executorService = executor
            }
        }

        ProcessingSettings(spec, Config.empty, PerformanceMonitor()).use { settings ->
            val descriptor = createRuleDescriptor(ruleProducer, Config.empty)
            val analyzer = Analyzer(
                settings,
                listOf(descriptor),
                processors = emptyList(),
                analysisMode = mode,
            )
            analyzer.run(settings.ktFiles)
        }
    } finally {
        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
    }
}
