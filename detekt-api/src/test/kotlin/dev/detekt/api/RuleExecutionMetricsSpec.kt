package dev.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class RuleExecutionMetricsSpec {

    @Nested
    inner class `profiling keys` {

        @Test
        fun `RULE_METRICS key has expected value`() {
            assertThat(RuleProfilingKeys.RULE_METRICS).isEqualTo("ruleExecutionMetrics")
        }

        @Test
        fun `FILE_EXECUTIONS key has expected value`() {
            assertThat(RuleProfilingKeys.FILE_EXECUTIONS).isEqualTo("ruleFileExecutions")
        }

        @Test
        fun `PARALLEL_DISABLED key has expected value`() {
            assertThat(RuleProfilingKeys.PARALLEL_DISABLED).isEqualTo("profilingParallelDisabled")
        }
    }

    @Nested
    inner class RuleExecutionMetric {

        @Test
        fun `creates metric with all properties`() {
            val metric = RuleExecutionMetric(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                totalDuration = 500.milliseconds,
                executionCount = 10,
                totalFindings = 5,
            )

            assertThat(metric.ruleId).isEqualTo("TestRule")
            assertThat(metric.ruleSetId.value).isEqualTo("TestRuleSet")
            assertThat(metric.totalDuration).isEqualTo(500.milliseconds)
            assertThat(metric.executionCount).isEqualTo(10)
            assertThat(metric.totalFindings).isEqualTo(5)
        }

        @Test
        fun `calculates average duration correctly`() {
            val metric = RuleExecutionMetric(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                totalDuration = 1.seconds,
                executionCount = 4,
                totalFindings = 0,
            )

            assertThat(metric.averageDuration).isEqualTo(250.milliseconds)
        }

        @Test
        fun `returns zero average duration when execution count is zero`() {
            val metric = RuleExecutionMetric(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                totalDuration = 500.milliseconds,
                executionCount = 0,
                totalFindings = 0,
            )

            assertThat(metric.averageDuration).isEqualTo(kotlin.time.Duration.ZERO)
        }

        @Test
        fun `handles single execution correctly`() {
            val metric = RuleExecutionMetric(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                totalDuration = 100.milliseconds,
                executionCount = 1,
                totalFindings = 3,
            )

            assertThat(metric.averageDuration).isEqualTo(100.milliseconds)
        }

        @Test
        fun `equals and hashCode work correctly`() {
            val metric1 = RuleExecutionMetric(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                totalDuration = 500.milliseconds,
                executionCount = 10,
                totalFindings = 5,
            )

            val metric2 = RuleExecutionMetric(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                totalDuration = 500.milliseconds,
                executionCount = 10,
                totalFindings = 5,
            )

            assertThat(metric1).isEqualTo(metric2)
            assertThat(metric1.hashCode()).isEqualTo(metric2.hashCode())
        }

        @Test
        fun `different metrics are not equal`() {
            val metric1 = RuleExecutionMetric(
                ruleId = "TestRule1",
                ruleSetId = RuleSetId("TestRuleSet"),
                totalDuration = 500.milliseconds,
                executionCount = 10,
                totalFindings = 5,
            )

            val metric2 = RuleExecutionMetric(
                ruleId = "TestRule2",
                ruleSetId = RuleSetId("TestRuleSet"),
                totalDuration = 500.milliseconds,
                executionCount = 10,
                totalFindings = 5,
            )

            assertThat(metric1).isNotEqualTo(metric2)
        }
    }

    @Nested
    inner class RuleFileExecution {

        @Test
        fun `creates execution with all properties`() {
            val execution = RuleFileExecution(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                filePath = "/path/to/File.kt",
                duration = 50.milliseconds,
                findings = 2,
            )

            assertThat(execution.ruleId).isEqualTo("TestRule")
            assertThat(execution.ruleSetId.value).isEqualTo("TestRuleSet")
            assertThat(execution.filePath).isEqualTo("/path/to/File.kt")
            assertThat(execution.duration).isEqualTo(50.milliseconds)
            assertThat(execution.findings).isEqualTo(2)
        }

        @Test
        fun `equals and hashCode work correctly`() {
            val execution1 = RuleFileExecution(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                filePath = "/path/to/File.kt",
                duration = 50.milliseconds,
                findings = 2,
            )

            val execution2 = RuleFileExecution(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                filePath = "/path/to/File.kt",
                duration = 50.milliseconds,
                findings = 2,
            )

            assertThat(execution1).isEqualTo(execution2)
            assertThat(execution1.hashCode()).isEqualTo(execution2.hashCode())
        }

        @Test
        fun `different executions are not equal`() {
            val execution1 = RuleFileExecution(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                filePath = "/path/to/File1.kt",
                duration = 50.milliseconds,
                findings = 2,
            )

            val execution2 = RuleFileExecution(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                filePath = "/path/to/File2.kt",
                duration = 50.milliseconds,
                findings = 2,
            )

            assertThat(execution1).isNotEqualTo(execution2)
        }

        @Test
        fun `handles zero findings and duration`() {
            val execution = RuleFileExecution(
                ruleId = "TestRule",
                ruleSetId = RuleSetId("TestRuleSet"),
                filePath = "/path/to/File.kt",
                duration = kotlin.time.Duration.ZERO,
                findings = 0,
            )

            assertThat(execution.duration).isEqualTo(kotlin.time.Duration.ZERO)
            assertThat(execution.findings).isEqualTo(0)
        }
    }
}
