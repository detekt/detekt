package dev.detekt.gradle.util

import java.io.File
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

/**
 * Utility for displaying rule execution profiling information.
 *
 * Shows the top N slowest rules with their total execution time,
 * call count, and average time per file.
 *
 * This is used by profiling tasks to display profiling results.
 */
object RuleProfilingReport {

    private const val DEFAULT_TOP_RULES_COUNT = 10
    private const val RULE_NAME_MAX_LENGTH = 40
    private const val ELLIPSIS = "..."
    private const val HEADER_FORMAT = "%-40s %10s %8s %10s %8s"
    private const val ROW_FORMAT = "%-40s %10s %8d %10s %8d"
    private const val HEADER_WIDTH = 80
    private const val MS_PER_SECOND = 1000.0
    private const val MIN_CSV_PARTS = 5

    /**
     * Parses profiling CSV files and aggregates metrics by rule.
     *
     * @param files List of CSV files to parse
     * @return List of aggregated metrics sorted by total duration (descending)
     */
    fun parseAndAggregate(files: List<File>): List<AggregatedRuleMetric> {
        val allEntries = files.flatMap { parseCsvFile(it) }

        return allEntries
            .groupBy { "${it.ruleSet}:${it.rule}" }
            .map { (_, entries) ->
                AggregatedRuleMetric(
                    ruleId = entries.first().rule,
                    ruleSetId = entries.first().ruleSet,
                    totalDuration = entries.sumOf { it.duration }.milliseconds,
                    executionCount = entries.size,
                    totalFindings = entries.sumOf { it.findings },
                )
            }
            .sortedByDescending { it.totalDuration }
    }

    /**
     * Parses profiling CSV files and aggregates metrics by rule.
     *
     * @param file Single CSV file to parse
     * @return List of aggregated metrics sorted by total duration (descending)
     */
    fun parseAndAggregate(file: File): List<AggregatedRuleMetric> = parseAndAggregate(listOf(file))

    /**
     * Renders a profiling report from aggregated metrics.
     *
     * @param metrics List of aggregated rule metrics
     * @param topRulesCount Number of top slowest rules to display (default: 10)
     * @param sourceCount Optional number of sources aggregated (for multi-project builds)
     * @return A formatted profiling report string, or null if no profiling data is available
     */
    fun render(
        metrics: List<AggregatedRuleMetric>,
        topRulesCount: Int = DEFAULT_TOP_RULES_COUNT,
        sourceCount: Int? = null,
    ): String? {
        if (metrics.isEmpty()) return null

        val topRules = metrics.take(topRulesCount)
        val totalTime = metrics.fold(Duration.ZERO) { acc, m -> acc + m.totalDuration }

        return buildString {
            appendLine()
            if (sourceCount != null && sourceCount > 1) {
                appendLine("Rule Execution Profile (Top $topRulesCount, Aggregated from $sourceCount sources):")
            } else {
                appendLine("Rule Execution Profile:")
            }
            appendLine("=======================")
            appendLine()
            appendLine(String.format(Locale.ROOT, HEADER_FORMAT, "Rule", "Total", "Calls", "Avg", "Findings"))
            appendLine("-".repeat(HEADER_WIDTH))

            for (metric in topRules) {
                appendLine(
                    String.format(
                        Locale.ROOT,
                        ROW_FORMAT,
                        formatRuleName(metric.ruleSetId, metric.ruleId),
                        formatDuration(metric.totalDuration),
                        metric.executionCount,
                        formatDuration(metric.averageDuration),
                        metric.totalFindings,
                    )
                )
            }

            appendLine("-".repeat(HEADER_WIDTH))
            appendLine("Total analysis time: ${formatDuration(totalTime)}")
            appendLine("Rules measured: ${metrics.size}")
        }
    }

    /**
     * Writes aggregated metrics as CSV to a file.
     *
     * @param metrics List of aggregated rule metrics
     * @param outputFile File to write CSV data to
     */
    fun writeCsv(metrics: List<AggregatedRuleMetric>, outputFile: File) {
        outputFile.writeText(
            buildString {
                appendLine("RuleSet,Rule,TotalDuration(ms),Calls,Findings")
                for (metric in metrics) {
                    appendLine(
                        "${metric.ruleSetId},${metric.ruleId}," +
                            "${metric.totalDuration.inWholeMilliseconds}," +
                            "${metric.executionCount},${metric.totalFindings}"
                    )
                }
            }
        )
    }

    private fun formatRuleName(ruleSetId: String, ruleId: String): String {
        val fullName = "$ruleSetId:$ruleId"
        return if (fullName.length > RULE_NAME_MAX_LENGTH) {
            fullName.take(RULE_NAME_MAX_LENGTH - ELLIPSIS.length) + ELLIPSIS
        } else {
            fullName
        }
    }

    private fun formatDuration(duration: Duration): String {
        val ms = duration.toDouble(DurationUnit.MILLISECONDS)
        return when {
            ms >= MS_PER_SECOND -> String.format(Locale.ROOT, "%.2fs", ms / MS_PER_SECOND)
            ms >= 1 -> String.format(Locale.ROOT, "%.1fms", ms)
            else -> String.format(Locale.ROOT, "%.2fms", ms)
        }
    }

    private fun parseCsvFile(file: File): List<ProfilingEntry> {
        val entries = mutableListOf<ProfilingEntry>()

        file.useLines { lines ->
            lines
                .drop(1) // Skip header
                .filter { it.isNotBlank() }
                .forEach { line ->
                    val parts = parseCsvLine(line)
                    if (parts.size >= MIN_CSV_PARTS) {
                        entries.add(
                            ProfilingEntry(
                                ruleSet = parts[0],
                                rule = parts[1],
                                file = parts[2],
                                duration = parts[3].toDoubleOrNull() ?: 0.0,
                                findings = parts[4].toIntOrNull() ?: 0,
                            )
                        )
                    }
                }
        }

        return entries
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false

        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes

                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }

                else -> current.append(char)
            }
        }
        result.add(current.toString())
        return result
    }

    private data class ProfilingEntry(
        val ruleSet: String,
        val rule: String,
        val file: String,
        val duration: Double,
        val findings: Int,
    )

    /**
     * Aggregated metrics for a single rule across all files.
     */
    data class AggregatedRuleMetric(
        val ruleId: String,
        val ruleSetId: String,
        val totalDuration: Duration,
        val executionCount: Int,
        val totalFindings: Int,
    ) {
        val averageDuration: Duration
            get() = if (executionCount > 0) totalDuration / executionCount else Duration.ZERO

        val displayName: String
            get() = if (ruleSetId.isNotEmpty()) "$ruleSetId:$ruleId" else ruleId
    }
}
