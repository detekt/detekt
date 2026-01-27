package dev.detekt.core.profiling

import dev.detekt.api.Detektion
import dev.detekt.api.RuleExecutionMetric
import dev.detekt.api.RuleFileExecution
import dev.detekt.api.RuleInstance
import dev.detekt.api.RuleProfilingKeys
import dev.detekt.api.RuleSetId
import dev.detekt.api.Severity
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import kotlin.time.Duration.Companion.milliseconds

class RuleProfilingListenerSpec {

    @Test
    fun `has correct id`() {
        val listener = RuleProfilingListener()
        assertThat(listener.id).isEqualTo("RuleProfilingListener")
    }

    @Test
    fun `has default priority`() {
        val listener = RuleProfilingListener()
        assertThat(listener.priority).isEqualTo(0)
    }

    @Nested
    inner class AfterRuleExecution {

        @Test
        fun `records single rule execution`() {
            val listener = RuleProfilingListener()
            val file = compileContentForTest("class Test")
            val rule = createRuleInstance("TestRule", "TestRuleSet")

            listener.afterRuleExecution(file, rule, findings = 2, duration = 100.milliseconds)

            val result = Detektion(emptyList(), listOf(rule))
            listener.onFinish(result)

            @Suppress("UNCHECKED_CAST")
            val executions = result.userData[RuleProfilingKeys.FILE_EXECUTIONS] as List<RuleFileExecution>
            assertThat(executions).hasSize(1)
            assertThat(executions[0].ruleId).isEqualTo("TestRule")
            assertThat(executions[0].ruleSetId.value).isEqualTo("TestRuleSet")
            assertThat(executions[0].duration).isEqualTo(100.milliseconds)
            assertThat(executions[0].findings).isEqualTo(2)
        }

        @Test
        fun `records multiple rule executions for same rule on different files`() {
            val listener = RuleProfilingListener()
            val file1 = compileContentForTest("class Test1", "Test1.kt")
            val file2 = compileContentForTest("class Test2", "Test2.kt")
            val rule = createRuleInstance("TestRule", "TestRuleSet")

            listener.afterRuleExecution(file1, rule, findings = 1, duration = 50.milliseconds)
            listener.afterRuleExecution(file2, rule, findings = 0, duration = 75.milliseconds)

            val result = Detektion(emptyList(), listOf(rule))
            listener.onFinish(result)

            @Suppress("UNCHECKED_CAST")
            val executions = result.userData[RuleProfilingKeys.FILE_EXECUTIONS] as List<RuleFileExecution>
            assertThat(executions).hasSize(2)
        }

        @Test
        fun `records executions for different rules`() {
            val listener = RuleProfilingListener()
            val file = compileContentForTest("class Test")
            val rule1 = createRuleInstance("Rule1", "RuleSet1")
            val rule2 = createRuleInstance("Rule2", "RuleSet2")

            listener.afterRuleExecution(file, rule1, findings = 1, duration = 100.milliseconds)
            listener.afterRuleExecution(file, rule2, findings = 2, duration = 200.milliseconds)

            val result = Detektion(emptyList(), listOf(rule1, rule2))
            listener.onFinish(result)

            @Suppress("UNCHECKED_CAST")
            val executions = result.userData[RuleProfilingKeys.FILE_EXECUTIONS] as List<RuleFileExecution>
            assertThat(executions).hasSize(2)
        }
    }

    @Nested
    inner class OnFinish {

        @Test
        fun `aggregates metrics by rule`() {
            val listener = RuleProfilingListener()
            val file1 = compileContentForTest("class Test1", "Test1.kt")
            val file2 = compileContentForTest("class Test2", "Test2.kt")
            val rule = createRuleInstance("TestRule", "TestRuleSet")

            listener.afterRuleExecution(file1, rule, findings = 1, duration = 50.milliseconds)
            listener.afterRuleExecution(file2, rule, findings = 2, duration = 100.milliseconds)

            val result = Detektion(emptyList(), listOf(rule))
            listener.onFinish(result)

            @Suppress("UNCHECKED_CAST")
            val metrics = result.userData[RuleProfilingKeys.RULE_METRICS] as List<RuleExecutionMetric>
            assertThat(metrics).hasSize(1)
            assertThat(metrics[0].ruleId).isEqualTo("TestRule")
            assertThat(metrics[0].ruleSetId.value).isEqualTo("TestRuleSet")
            assertThat(metrics[0].totalDuration).isEqualTo(150.milliseconds)
            assertThat(metrics[0].executionCount).isEqualTo(2)
            assertThat(metrics[0].totalFindings).isEqualTo(3)
        }

        @Test
        fun `sorts metrics by total duration descending`() {
            val listener = RuleProfilingListener()
            val file = compileContentForTest("class Test")
            val fastRule = createRuleInstance("FastRule", "RuleSet")
            val slowRule = createRuleInstance("SlowRule", "RuleSet")

            listener.afterRuleExecution(file, fastRule, findings = 0, duration = 10.milliseconds)
            listener.afterRuleExecution(file, slowRule, findings = 0, duration = 100.milliseconds)

            val result = Detektion(emptyList(), listOf(fastRule, slowRule))
            listener.onFinish(result)

            @Suppress("UNCHECKED_CAST")
            val metrics = result.userData[RuleProfilingKeys.RULE_METRICS] as List<RuleExecutionMetric>
            assertThat(metrics).hasSize(2)
            assertThat(metrics[0].ruleId).isEqualTo("SlowRule")
            assertThat(metrics[1].ruleId).isEqualTo("FastRule")
        }

        @Test
        fun `stores file executions in userData`() {
            val listener = RuleProfilingListener()
            val file = compileContentForTest("class Test")
            val rule = createRuleInstance("TestRule", "TestRuleSet")

            listener.afterRuleExecution(file, rule, findings = 1, duration = 50.milliseconds)

            val result = Detektion(emptyList(), listOf(rule))
            listener.onFinish(result)

            assertThat(result.userData).containsKey(RuleProfilingKeys.FILE_EXECUTIONS)
        }

        @Test
        fun `stores rule metrics in userData`() {
            val listener = RuleProfilingListener()
            val file = compileContentForTest("class Test")
            val rule = createRuleInstance("TestRule", "TestRuleSet")

            listener.afterRuleExecution(file, rule, findings = 1, duration = 50.milliseconds)

            val result = Detektion(emptyList(), listOf(rule))
            listener.onFinish(result)

            assertThat(result.userData).containsKey(RuleProfilingKeys.RULE_METRICS)
        }

        @Test
        fun `returns the same detektion instance`() {
            val listener = RuleProfilingListener()
            val result = Detektion(emptyList(), emptyList())
            val returnedResult = listener.onFinish(result)

            assertThat(returnedResult).isSameAs(result)
        }

        @Test
        fun `handles empty execution list`() {
            val listener = RuleProfilingListener()
            val result = Detektion(emptyList(), emptyList())
            listener.onFinish(result)

            @Suppress("UNCHECKED_CAST")
            val metrics = result.userData[RuleProfilingKeys.RULE_METRICS] as List<RuleExecutionMetric>

            @Suppress("UNCHECKED_CAST")
            val executions = result.userData[RuleProfilingKeys.FILE_EXECUTIONS] as List<RuleFileExecution>

            assertThat(metrics).isEmpty()
            assertThat(executions).isEmpty()
        }

        @Test
        fun `calculates correct average duration in metrics`() {
            val listener = RuleProfilingListener()
            val file1 = compileContentForTest("class Test1", "Test1.kt")
            val file2 = compileContentForTest("class Test2", "Test2.kt")
            val file3 = compileContentForTest("class Test3", "Test3.kt")
            val rule = createRuleInstance("TestRule", "TestRuleSet")

            listener.afterRuleExecution(file1, rule, findings = 0, duration = 30.milliseconds)
            listener.afterRuleExecution(file2, rule, findings = 0, duration = 60.milliseconds)
            listener.afterRuleExecution(file3, rule, findings = 0, duration = 90.milliseconds)

            val result = Detektion(emptyList(), listOf(rule))
            listener.onFinish(result)

            @Suppress("UNCHECKED_CAST")
            val metrics = result.userData[RuleProfilingKeys.RULE_METRICS] as List<RuleExecutionMetric>
            assertThat(metrics[0].averageDuration).isEqualTo(60.milliseconds)
        }
    }

    @Nested
    inner class `thread safety` {

        @Test
        fun `can handle concurrent executions from different threads`() {
            val listener = RuleProfilingListener()
            val rule = createRuleInstance("TestRule", "TestRuleSet")
            val threads = (1..10).map { index ->
                Thread {
                    val file = compileContentForTest("class Test$index", "Test$index.kt")
                    listener.afterRuleExecution(file, rule, findings = 1, duration = 10.milliseconds)
                }
            }

            threads.forEach { it.start() }
            threads.forEach { it.join() }

            val result = Detektion(emptyList(), listOf(rule))
            listener.onFinish(result)

            @Suppress("UNCHECKED_CAST")
            val executions = result.userData[RuleProfilingKeys.FILE_EXECUTIONS] as List<RuleFileExecution>
            assertThat(executions).hasSize(10)
        }
    }

    private fun createRuleInstance(id: String, ruleSetId: String): RuleInstance =
        RuleInstance(
            id = id,
            ruleSetId = RuleSetId(ruleSetId),
            url = URI("https://example.org/"),
            description = "Test rule",
            severity = Severity.Error,
            active = true,
        )
}
