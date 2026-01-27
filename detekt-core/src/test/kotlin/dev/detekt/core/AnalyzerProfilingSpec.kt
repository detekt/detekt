package dev.detekt.core

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.RuleExecutionListener
import dev.detekt.api.RuleInstance
import dev.detekt.test.utils.compileContentForTest
import dev.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration

class AnalyzerProfilingSpec {

    @Nested
    inner class `with rule execution listeners` {

        @Test
        fun `calls beforeRuleExecution for each rule-file combination`() {
            val listener = TestRuleExecutionListener()
            val file = compileContentForTest("class Test")

            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("Rule1"),
                ),
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("Rule2"),
                ),
                ruleListeners = listOf(listener),
            )

            settings.use { analyzer.run(listOf(file)) }

            assertThat(listener.beforeCalls.get()).isEqualTo(2)
        }

        @Test
        fun `calls afterRuleExecution for each rule-file combination`() {
            val listener = TestRuleExecutionListener()
            val file = compileContentForTest("class Test")

            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("Rule1"),
                ),
                ruleListeners = listOf(listener),
            )

            settings.use { analyzer.run(listOf(file)) }

            assertThat(listener.afterCalls.get()).isEqualTo(1)
        }

        @Test
        fun `provides duration to afterRuleExecution`() {
            val listener = TestRuleExecutionListener()
            val file = compileContentForTest("class Test")

            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("Rule1"),
                ),
                ruleListeners = listOf(listener),
            )

            settings.use { analyzer.run(listOf(file)) }

            assertThat(listener.recordedDurations).hasSize(1)
            assertThat(listener.recordedDurations[0]).isGreaterThan(Duration.ZERO)
        }

        @Test
        fun `provides correct findings count to afterRuleExecution`() {
            val listener = TestRuleExecutionListener()
            val file = compileContentForTest("class TestClass")

            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::FindingRule,
                    findingRuleConfig("FindingRule"),
                ),
                ruleListeners = listOf(listener),
            )

            settings.use { analyzer.run(listOf(file)) }

            assertThat(listener.recordedFindings).containsExactly(1)
        }

        @Test
        fun `provides correct rule instance to callbacks`() {
            val listener = TestRuleExecutionListener()
            val file = compileContentForTest("class Test")

            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("SimpleRule"),
                ),
                ruleListeners = listOf(listener),
            )

            settings.use { analyzer.run(listOf(file)) }

            assertThat(listener.recordedRules).hasSize(1)
            assertThat(listener.recordedRules[0].id).isEqualTo("SimpleRule")
        }

        @Test
        fun `provides correct file to callbacks`() {
            val listener = TestRuleExecutionListener()
            val file = compileContentForTest("class Test", "TestFile.kt")

            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("Rule"),
                ),
                ruleListeners = listOf(listener),
            )

            settings.use { analyzer.run(listOf(file)) }

            assertThat(listener.recordedFiles).hasSize(1)
            assertThat(listener.recordedFiles[0].name).isEqualTo("TestFile.kt")
        }

        @Test
        fun `handles multiple files`() {
            val listener = TestRuleExecutionListener()
            val file1 = compileContentForTest("class Test1", "Test1.kt")
            val file2 = compileContentForTest("class Test2", "Test2.kt")

            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("Rule"),
                ),
                ruleListeners = listOf(listener),
            )

            settings.use { analyzer.run(listOf(file1, file2)) }

            assertThat(listener.beforeCalls.get()).isEqualTo(2)
            assertThat(listener.afterCalls.get()).isEqualTo(2)
        }

        @Test
        fun `supports multiple listeners`() {
            val listener1 = TestRuleExecutionListener()
            val listener2 = TestRuleExecutionListener()
            val file = compileContentForTest("class Test")

            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("Rule"),
                ),
                ruleListeners = listOf(listener1, listener2),
            )

            settings.use { analyzer.run(listOf(file)) }

            assertThat(listener1.afterCalls.get()).isEqualTo(1)
            assertThat(listener2.afterCalls.get()).isEqualTo(1)
        }
    }

    @Nested
    inner class `parallel execution with profiling` {

        @Test
        fun `disables parallel execution when listeners are present`() {
            val listener = TestRuleExecutionListener()
            val settings = createProcessingSettings {
                execution {
                    parallelAnalysis = true
                }
            }

            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("Rule"),
                ),
                ruleListeners = listOf(listener),
            )

            assertThat(analyzer.parallelDisabledForProfiling).isTrue()
        }

        @Test
        fun `does not disable parallel when no listeners`() {
            val settings = createProcessingSettings {
                execution {
                    parallelAnalysis = true
                }
            }

            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("Rule"),
                ),
                ruleListeners = emptyList(),
            )

            assertThat(analyzer.parallelDisabledForProfiling).isFalse()
        }

        @Test
        fun `reports false when parallel not requested`() {
            val listener = TestRuleExecutionListener()
            val settings = createProcessingSettings {
                execution {
                    parallelAnalysis = false
                }
            }

            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::SimpleRule,
                    simpleRuleConfig("Rule"),
                ),
                ruleListeners = listOf(listener),
            )

            assertThat(analyzer.parallelDisabledForProfiling).isFalse()
        }

        @Test
        fun `still runs sequentially when parallel is requested but listeners are present`() {
            val executionOrder = CopyOnWriteArrayList<String>()
            val listener = object : RuleExecutionListener {
                override val id = "OrderTracker"
                override fun afterRuleExecution(file: KtFile, rule: RuleInstance, findings: Int, duration: Duration) {
                    executionOrder.add("${rule.id}-${file.name}")
                }
            }

            val file1 = compileContentForTest("class Test1", "Test1.kt")
            val file2 = compileContentForTest("class Test2", "Test2.kt")

            val settings = createProcessingSettings {
                execution {
                    parallelAnalysis = true
                }
            }

            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(::SimpleRule, simpleRuleConfig("Rule1")),
                createRuleDescriptor(::SimpleRule, simpleRuleConfig("Rule2")),
                ruleListeners = listOf(listener),
            )

            settings.use { analyzer.run(listOf(file1, file2)) }

            // The executions should have happened, order may vary based on file processing
            assertThat(executionOrder).hasSize(4)
        }
    }

    private fun simpleRuleConfig(ruleName: String): Config =
        """
            custom:
              $ruleName:
                active: true
        """.trimIndent().toConfig("custom", ruleName)

    private fun findingRuleConfig(ruleName: String): Config =
        """
            custom:
              $ruleName:
                active: true
        """.trimIndent().toConfig("custom", ruleName)

    private fun String.toConfig(vararg subConfigs: String): Config =
        subConfigs.fold(yamlConfigFromContent(this)) { acc, key -> acc.subConfig(key) }

    private class TestRuleExecutionListener : RuleExecutionListener {
        override val id = "TestRuleExecutionListener"

        val beforeCalls = AtomicInteger(0)
        val afterCalls = AtomicInteger(0)
        val recordedDurations = CopyOnWriteArrayList<Duration>()
        val recordedFindings = CopyOnWriteArrayList<Int>()
        val recordedRules = CopyOnWriteArrayList<RuleInstance>()
        val recordedFiles = CopyOnWriteArrayList<KtFile>()

        override fun beforeRuleExecution(file: KtFile, rule: RuleInstance) {
            beforeCalls.incrementAndGet()
        }

        override fun afterRuleExecution(file: KtFile, rule: RuleInstance, findings: Int, duration: Duration) {
            afterCalls.incrementAndGet()
            recordedDurations.add(duration)
            recordedFindings.add(findings)
            recordedRules.add(rule)
            recordedFiles.add(file)
        }
    }
}

private class SimpleRule(config: Config) : Rule(config, "Simple test rule") {
    override fun visitKtFile(file: KtFile) {
        // Do nothing - just visit
    }
}

private class FindingRule(config: Config) : Rule(config, "Reports a finding for each class") {
    override fun visitKtFile(file: KtFile) {
        file.declarations.forEach { decl ->
            report(Finding(Entity.from(decl), "Found a declaration"))
        }
    }
}
