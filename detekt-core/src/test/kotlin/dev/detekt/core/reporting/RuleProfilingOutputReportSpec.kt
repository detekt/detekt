package dev.detekt.core.reporting

import dev.detekt.api.RuleFileExecution
import dev.detekt.api.RuleProfilingKeys
import dev.detekt.api.RuleSetId
import dev.detekt.api.testfixtures.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

class RuleProfilingOutputReportSpec {

    private val report = RuleProfilingOutputReport()

    @Test
    fun `has correct id`() {
        assertThat(report.id).isEqualTo("profiling")
    }

    @Test
    fun `has default priority`() {
        assertThat(report.priority).isEqualTo(0)
    }

    @Nested
    inner class Render {

        @Test
        fun `returns null when no profiling data is available`() {
            val detektion = TestDetektion()

            val result = report.render(detektion)

            assertThat(result).isNull()
        }

        @Test
        fun `returns null when profiling data is empty`() {
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to emptyList<RuleFileExecution>())
            )

            val result = report.render(detektion)

            assertThat(result).isNull()
        }

        @Test
        fun `generates CSV header`() {
            val executions = listOf(
                RuleFileExecution(
                    ruleId = "TestRule",
                    ruleSetId = RuleSetId("TestRuleSet"),
                    filePath = "/path/to/File.kt",
                    duration = 50.milliseconds,
                    findings = 2,
                )
            )
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to executions)
            )

            val result = report.render(detektion)

            assertThat(result).isNotNull()
            assertThat(result!!.lines().first()).isEqualTo("RuleSet,Rule,File,Duration(ms),Findings")
        }

        @Test
        fun `generates CSV with execution data`() {
            val executions = listOf(
                RuleFileExecution(
                    ruleId = "TestRule",
                    ruleSetId = RuleSetId("TestRuleSet"),
                    filePath = "/path/to/File.kt",
                    duration = 50.5.milliseconds,
                    findings = 2,
                )
            )
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to executions)
            )

            val result = report.render(detektion)

            assertThat(result).isNotNull()
            val lines = result!!.lines()
            assertThat(lines[1]).isEqualTo("TestRuleSet,TestRule,/path/to/File.kt,50.500,2")
        }

        @Test
        fun `sorts executions by ruleset, rule, then file`() {
            val executions = listOf(
                RuleFileExecution("RuleB", RuleSetId("SetB"), "/path/FileB.kt", 10.milliseconds, 0),
                RuleFileExecution("RuleA", RuleSetId("SetA"), "/path/FileA.kt", 20.milliseconds, 0),
                RuleFileExecution("RuleA", RuleSetId("SetA"), "/path/FileB.kt", 30.milliseconds, 0),
                RuleFileExecution("RuleB", RuleSetId("SetA"), "/path/FileA.kt", 40.milliseconds, 0),
            )
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to executions)
            )

            val result = report.render(detektion)

            assertThat(result).isNotNull()
            val dataLines = result!!.lines().drop(1).filter { it.isNotEmpty() }
            assertThat(dataLines[0]).startsWith("SetA,RuleA,/path/FileA.kt")
            assertThat(dataLines[1]).startsWith("SetA,RuleA,/path/FileB.kt")
            assertThat(dataLines[2]).startsWith("SetA,RuleB,/path/FileA.kt")
            assertThat(dataLines[3]).startsWith("SetB,RuleB,/path/FileB.kt")
        }

        @Test
        fun `escapes values containing commas`() {
            val executions = listOf(
                RuleFileExecution(
                    ruleId = "TestRule",
                    ruleSetId = RuleSetId("TestRuleSet"),
                    filePath = "/path/to/File,With,Commas.kt",
                    duration = 50.milliseconds,
                    findings = 0,
                )
            )
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to executions)
            )

            val result = report.render(detektion)

            assertThat(result).isNotNull()
            val dataLine = result!!.lines()[1]
            assertThat(dataLine).contains("\"/path/to/File,With,Commas.kt\"")
        }

        @Test
        fun `escapes values containing quotes`() {
            val executions = listOf(
                RuleFileExecution(
                    ruleId = "TestRule",
                    ruleSetId = RuleSetId("TestRuleSet"),
                    filePath = "/path/to/File\"With\"Quotes.kt",
                    duration = 50.milliseconds,
                    findings = 0,
                )
            )
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to executions)
            )

            val result = report.render(detektion)

            assertThat(result).isNotNull()
            val dataLine = result!!.lines()[1]
            assertThat(dataLine).contains("\"/path/to/File\"\"With\"\"Quotes.kt\"")
        }

        @Test
        fun `escapes values containing newlines`() {
            val executions = listOf(
                RuleFileExecution(
                    ruleId = "TestRule",
                    ruleSetId = RuleSetId("TestRuleSet"),
                    filePath = "/path/to/File\nWithNewline.kt",
                    duration = 50.milliseconds,
                    findings = 0,
                )
            )
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to executions)
            )

            val result = report.render(detektion)

            assertThat(result).isNotNull()
            val dataLine = result!!.lines()[1]
            assertThat(dataLine).startsWith("TestRuleSet,TestRule,\"/path/to/File")
        }

        @Test
        fun `formats duration with millisecond precision`() {
            val executions = listOf(
                RuleFileExecution(
                    ruleId = "TestRule",
                    ruleSetId = RuleSetId("TestRuleSet"),
                    filePath = "/path/to/File.kt",
                    duration = 123.456789.milliseconds,
                    findings = 0,
                )
            )
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to executions)
            )

            val result = report.render(detektion)

            assertThat(result).isNotNull()
            val dataLine = result!!.lines()[1]
            // Should be formatted to 3 decimal places
            assertThat(dataLine).contains("123.457")
        }

        @Test
        fun `handles multiple executions`() {
            val executions = listOf(
                RuleFileExecution("Rule1", RuleSetId("Set1"), "/File1.kt", 10.milliseconds, 1),
                RuleFileExecution("Rule2", RuleSetId("Set2"), "/File2.kt", 20.milliseconds, 2),
                RuleFileExecution("Rule3", RuleSetId("Set3"), "/File3.kt", 30.milliseconds, 3),
            )
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to executions)
            )

            val result = report.render(detektion)

            assertThat(result).isNotNull()
            val lines = result!!.lines().filter { it.isNotEmpty() }
            assertThat(lines).hasSize(4) // 1 header + 3 data lines
        }

        @Test
        fun `handles zero duration correctly`() {
            val executions = listOf(
                RuleFileExecution(
                    ruleId = "TestRule",
                    ruleSetId = RuleSetId("TestRuleSet"),
                    filePath = "/path/to/File.kt",
                    duration = kotlin.time.Duration.ZERO,
                    findings = 0,
                )
            )
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to executions)
            )

            val result = report.render(detektion)

            assertThat(result).isNotNull()
            val dataLine = result!!.lines()[1]
            assertThat(dataLine).contains("0.000")
        }

        @Test
        fun `does not escape values without special characters`() {
            val executions = listOf(
                RuleFileExecution(
                    ruleId = "SimpleRule",
                    ruleSetId = RuleSetId("SimpleRuleSet"),
                    filePath = "/simple/path/File.kt",
                    duration = 50.milliseconds,
                    findings = 0,
                )
            )
            val detektion = TestDetektion(
                userData = mapOf(RuleProfilingKeys.FILE_EXECUTIONS to executions)
            )

            val result = report.render(detektion)

            assertThat(result).isNotNull()
            val dataLine = result!!.lines()[1]
            assertThat(dataLine).doesNotContain("\"")
            assertThat(dataLine).isEqualTo("SimpleRuleSet,SimpleRule,/simple/path/File.kt,50.000,0")
        }
    }
}
