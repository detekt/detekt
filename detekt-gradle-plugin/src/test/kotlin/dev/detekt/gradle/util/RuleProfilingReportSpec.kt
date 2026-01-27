package dev.detekt.gradle.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class RuleProfilingReportSpec {

    @TempDir
    lateinit var tempDir: File

    @Nested
    inner class `parseAndAggregate with single file` {

        @Test
        fun `parses CSV header and data correctly`() {
            val csvFile = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                TestRuleSet,TestRule,/path/to/File.kt,50.0,2
                """.trimIndent()
            )

            val metrics = RuleProfilingReport.parseAndAggregate(csvFile)

            assertThat(metrics).hasSize(1)
            assertThat(metrics[0].ruleId).isEqualTo("TestRule")
            assertThat(metrics[0].ruleSetId).isEqualTo("TestRuleSet")
            assertThat(metrics[0].totalDuration).isEqualTo(50.milliseconds)
            assertThat(metrics[0].executionCount).isEqualTo(1)
            assertThat(metrics[0].totalFindings).isEqualTo(2)
        }

        @Test
        fun `aggregates multiple executions of same rule`() {
            val csvFile = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                TestRuleSet,TestRule,/path/to/File1.kt,30.0,1
                TestRuleSet,TestRule,/path/to/File2.kt,70.0,2
                """.trimIndent()
            )

            val metrics = RuleProfilingReport.parseAndAggregate(csvFile)

            assertThat(metrics).hasSize(1)
            assertThat(metrics[0].totalDuration).isEqualTo(100.milliseconds)
            assertThat(metrics[0].executionCount).isEqualTo(2)
            assertThat(metrics[0].totalFindings).isEqualTo(3)
        }

        @Test
        fun `sorts by total duration descending`() {
            val csvFile = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                Set,FastRule,/File.kt,10.0,0
                Set,SlowRule,/File.kt,100.0,0
                Set,MediumRule,/File.kt,50.0,0
                """.trimIndent()
            )

            val metrics = RuleProfilingReport.parseAndAggregate(csvFile)

            assertThat(metrics).hasSize(3)
            assertThat(metrics[0].ruleId).isEqualTo("SlowRule")
            assertThat(metrics[1].ruleId).isEqualTo("MediumRule")
            assertThat(metrics[2].ruleId).isEqualTo("FastRule")
        }

        @Test
        fun `handles quoted values with commas`() {
            val csvFile = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                TestRuleSet,TestRule,"/path/to/File,With,Commas.kt",50.0,2
                """.trimIndent()
            )

            val metrics = RuleProfilingReport.parseAndAggregate(csvFile)

            assertThat(metrics).hasSize(1)
        }

        @Test
        fun `handles empty file`() {
            val csvFile = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                """.trimIndent()
            )

            val metrics = RuleProfilingReport.parseAndAggregate(csvFile)

            assertThat(metrics).isEmpty()
        }

        @Test
        fun `ignores blank lines`() {
            val csvFile = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings

                TestRuleSet,TestRule,/File.kt,50.0,2

                """.trimIndent()
            )

            val metrics = RuleProfilingReport.parseAndAggregate(csvFile)

            assertThat(metrics).hasSize(1)
        }

        @Test
        fun `handles invalid duration gracefully`() {
            val csvFile = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                TestRuleSet,TestRule,/File.kt,invalid,2
                """.trimIndent()
            )

            val metrics = RuleProfilingReport.parseAndAggregate(csvFile)

            assertThat(metrics).hasSize(1)
            assertThat(metrics[0].totalDuration).isEqualTo(kotlin.time.Duration.ZERO)
        }

        @Test
        fun `handles invalid findings count gracefully`() {
            val csvFile = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                TestRuleSet,TestRule,/File.kt,50.0,invalid
                """.trimIndent()
            )

            val metrics = RuleProfilingReport.parseAndAggregate(csvFile)

            assertThat(metrics).hasSize(1)
            assertThat(metrics[0].totalFindings).isEqualTo(0)
        }
    }

    @Nested
    inner class `parseAndAggregate with multiple files` {

        @Test
        fun `aggregates metrics across files`() {
            val csvFile1 = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                TestRuleSet,TestRule,/File1.kt,50.0,2
                """.trimIndent(),
                "file1.csv"
            )
            val csvFile2 = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                TestRuleSet,TestRule,/File2.kt,100.0,3
                """.trimIndent(),
                "file2.csv"
            )

            val metrics = RuleProfilingReport.parseAndAggregate(listOf(csvFile1, csvFile2))

            assertThat(metrics).hasSize(1)
            assertThat(metrics[0].totalDuration).isEqualTo(150.milliseconds)
            assertThat(metrics[0].executionCount).isEqualTo(2)
            assertThat(metrics[0].totalFindings).isEqualTo(5)
        }

        @Test
        fun `handles different rules across files`() {
            val csvFile1 = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                Set1,Rule1,/File1.kt,50.0,1
                """.trimIndent(),
                "file1.csv"
            )
            val csvFile2 = createCsvFile(
                """
                RuleSet,Rule,File,Duration(ms),Findings
                Set2,Rule2,/File2.kt,100.0,2
                """.trimIndent(),
                "file2.csv"
            )

            val metrics = RuleProfilingReport.parseAndAggregate(listOf(csvFile1, csvFile2))

            assertThat(metrics).hasSize(2)
        }

        @Test
        fun `handles empty file list`() {
            val metrics = RuleProfilingReport.parseAndAggregate(emptyList())

            assertThat(metrics).isEmpty()
        }
    }

    @Nested
    inner class `render` {

        @Test
        fun `returns null for empty metrics`() {
            val result = RuleProfilingReport.render(emptyList())

            assertThat(result).isNull()
        }

        @Test
        fun `renders header`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric(
                    ruleId = "TestRule",
                    ruleSetId = "TestRuleSet",
                    totalDuration = 100.milliseconds,
                    executionCount = 5,
                    totalFindings = 3,
                )
            )

            val result = RuleProfilingReport.render(metrics)

            assertThat(result).isNotNull()
            assertThat(result).contains("Rule Execution Profile:")
            assertThat(result).contains("Rule")
            assertThat(result).contains("Total")
            assertThat(result).contains("Calls")
            assertThat(result).contains("Avg")
            assertThat(result).contains("Findings")
        }

        @Test
        fun `renders rule metrics`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric(
                    ruleId = "TestRule",
                    ruleSetId = "TestRuleSet",
                    totalDuration = 100.milliseconds,
                    executionCount = 5,
                    totalFindings = 3,
                )
            )

            val result = RuleProfilingReport.render(metrics)

            assertThat(result).isNotNull()
            assertThat(result).contains("TestRuleSet:TestRule")
            assertThat(result).contains("5")
            assertThat(result).contains("3")
        }

        @Test
        fun `limits output to top N rules`() {
            // Create metrics and sort by total duration descending (as parseAndAggregate would)
            val metrics = (1..20).map { index ->
                RuleProfilingReport.AggregatedRuleMetric(
                    ruleId = "Rule$index",
                    ruleSetId = "RuleSet",
                    totalDuration = (index * 10).milliseconds,
                    executionCount = 1,
                    totalFindings = 0,
                )
            }.sortedByDescending { it.totalDuration }

            val result = RuleProfilingReport.render(metrics, topRulesCount = 5)

            assertThat(result).isNotNull()
            assertThat(result).contains("RuleSet:Rule20") // Slowest
            assertThat(result).contains("RuleSet:Rule16") // 5th slowest
            assertThat(result).doesNotContain("RuleSet:Rule15") // 6th slowest, should not be shown
            assertThat(result).doesNotContain("RuleSet:Rule1 ") // Fastest, should not be shown (note space to avoid matching Rule10+)
        }

        @Test
        fun `shows total analysis time`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric("Rule1", "Set", 100.milliseconds, 1, 0),
                RuleProfilingReport.AggregatedRuleMetric("Rule2", "Set", 200.milliseconds, 1, 0),
            )

            val result = RuleProfilingReport.render(metrics)

            assertThat(result).isNotNull()
            assertThat(result).contains("Total analysis time:")
        }

        @Test
        fun `shows rules measured count`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric("Rule1", "Set", 100.milliseconds, 1, 0),
                RuleProfilingReport.AggregatedRuleMetric("Rule2", "Set", 200.milliseconds, 1, 0),
            )

            val result = RuleProfilingReport.render(metrics)

            assertThat(result).isNotNull()
            assertThat(result).contains("Rules measured: 2")
        }

        @Test
        fun `truncates long rule names`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric(
                    ruleId = "VeryLongRuleNameThatExceedsTheMaximumAllowedLength",
                    ruleSetId = "VeryLongRuleSetName",
                    totalDuration = 100.milliseconds,
                    executionCount = 1,
                    totalFindings = 0,
                )
            )

            val result = RuleProfilingReport.render(metrics)

            assertThat(result).isNotNull()
            assertThat(result).contains("...")
        }

        @Test
        fun `formats duration in seconds when over 1 second`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric(
                    ruleId = "SlowRule",
                    ruleSetId = "Set",
                    totalDuration = 2.5.seconds,
                    executionCount = 1,
                    totalFindings = 0,
                )
            )

            val result = RuleProfilingReport.render(metrics)

            assertThat(result).isNotNull()
            assertThat(result).contains("2.50s")
        }

        @Test
        fun `formats duration in milliseconds when under 1 second`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric(
                    ruleId = "FastRule",
                    ruleSetId = "Set",
                    totalDuration = 500.milliseconds,
                    executionCount = 1,
                    totalFindings = 0,
                )
            )

            val result = RuleProfilingReport.render(metrics)

            assertThat(result).isNotNull()
            assertThat(result).contains("500.0ms")
        }

        @Test
        fun `includes source count in header when provided`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric("Rule", "Set", 100.milliseconds, 1, 0)
            )

            val result = RuleProfilingReport.render(metrics, sourceCount = 5)

            assertThat(result).isNotNull()
            assertThat(result).contains("Aggregated from 5 sources")
        }

        @Test
        fun `does not include source count for single source`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric("Rule", "Set", 100.milliseconds, 1, 0)
            )

            val result = RuleProfilingReport.render(metrics, sourceCount = 1)

            assertThat(result).isNotNull()
            assertThat(result).doesNotContain("Aggregated from")
        }
    }

    @Nested
    inner class `writeCsv` {

        @Test
        fun `writes header`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric("Rule", "Set", 100.milliseconds, 1, 0)
            )
            val outputFile = File(tempDir, "output.csv")

            RuleProfilingReport.writeCsv(metrics, outputFile)

            val content = outputFile.readText()
            assertThat(content.lines().first()).isEqualTo("RuleSet,Rule,TotalDuration(ms),Calls,Findings")
        }

        @Test
        fun `writes metrics data`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric(
                    ruleId = "TestRule",
                    ruleSetId = "TestRuleSet",
                    totalDuration = 150.milliseconds,
                    executionCount = 10,
                    totalFindings = 5,
                )
            )
            val outputFile = File(tempDir, "output.csv")

            RuleProfilingReport.writeCsv(metrics, outputFile)

            val content = outputFile.readText()
            val dataLine = content.lines()[1]
            assertThat(dataLine).isEqualTo("TestRuleSet,TestRule,150,10,5")
        }

        @Test
        fun `writes multiple metrics`() {
            val metrics = listOf(
                RuleProfilingReport.AggregatedRuleMetric("Rule1", "Set1", 100.milliseconds, 5, 2),
                RuleProfilingReport.AggregatedRuleMetric("Rule2", "Set2", 200.milliseconds, 10, 3),
            )
            val outputFile = File(tempDir, "output.csv")

            RuleProfilingReport.writeCsv(metrics, outputFile)

            val content = outputFile.readText()
            val lines = content.lines().filter { it.isNotEmpty() }
            assertThat(lines).hasSize(3) // 1 header + 2 data lines
        }
    }

    @Nested
    inner class `AggregatedRuleMetric` {

        @Test
        fun `calculates average duration correctly`() {
            val metric = RuleProfilingReport.AggregatedRuleMetric(
                ruleId = "Rule",
                ruleSetId = "Set",
                totalDuration = 100.milliseconds,
                executionCount = 4,
                totalFindings = 0,
            )

            assertThat(metric.averageDuration).isEqualTo(25.milliseconds)
        }

        @Test
        fun `returns zero average duration when no executions`() {
            val metric = RuleProfilingReport.AggregatedRuleMetric(
                ruleId = "Rule",
                ruleSetId = "Set",
                totalDuration = 100.milliseconds,
                executionCount = 0,
                totalFindings = 0,
            )

            assertThat(metric.averageDuration).isEqualTo(kotlin.time.Duration.ZERO)
        }

        @Test
        fun `generates display name with ruleset`() {
            val metric = RuleProfilingReport.AggregatedRuleMetric(
                ruleId = "TestRule",
                ruleSetId = "TestRuleSet",
                totalDuration = 100.milliseconds,
                executionCount = 1,
                totalFindings = 0,
            )

            assertThat(metric.displayName).isEqualTo("TestRuleSet:TestRule")
        }

        @Test
        fun `generates display name without ruleset when empty`() {
            val metric = RuleProfilingReport.AggregatedRuleMetric(
                ruleId = "TestRule",
                ruleSetId = "",
                totalDuration = 100.milliseconds,
                executionCount = 1,
                totalFindings = 0,
            )

            assertThat(metric.displayName).isEqualTo("TestRule")
        }
    }

    private fun createCsvFile(content: String, filename: String = "test.csv"): File {
        val file = File(tempDir, filename)
        file.writeText(content)
        return file
    }
}
