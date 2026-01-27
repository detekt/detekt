package dev.detekt.core.reporting

import dev.detekt.api.Detektion
import dev.detekt.api.OutputReport
import dev.detekt.api.RuleFileExecution
import dev.detekt.api.RuleProfilingKeys
import java.util.Locale
import kotlin.time.DurationUnit

/**
 * Output report that generates a CSV file with detailed rule execution profiling data.
 *
 * The CSV contains one row per rule-file execution with the following columns:
 * - RuleSet: The rule set ID
 * - Rule: The rule ID
 * - File: The file path that was analyzed
 * - Duration(ms): Execution time in milliseconds
 * - Findings: Number of findings reported
 *
 * Use with `--report profiling:path/to/file.csv` to generate this report.
 *
 * This report is only rendered when profiling data is available.
 */
class RuleProfilingOutputReport : OutputReport {

    override val id: String = "profiling"
    override val priority: Int = 0

    override fun render(detektion: Detektion): String? {
        @Suppress("UNCHECKED_CAST")
        val executions = detektion.userData[RuleProfilingKeys.FILE_EXECUTIONS] as? List<RuleFileExecution>
            ?: return null

        if (executions.isEmpty()) return null

        val sortedExecutions = executions.sortedWith(
            compareBy(
                { it.ruleSetId.value },
                { it.ruleId },
                { it.filePath },
            )
        )

        return buildString {
            appendLine("RuleSet,Rule,File,Duration(ms),Findings")
            for (execution in sortedExecutions) {
                appendLine(
                    listOf(
                        escapeCsv(execution.ruleSetId.value),
                        escapeCsv(execution.ruleId),
                        escapeCsv(execution.filePath),
                        String.format(Locale.ROOT, "%.3f", execution.duration.toDouble(DurationUnit.MILLISECONDS)),
                        execution.findings.toString(),
                    ).joinToString(",")
                )
            }
        }
    }

    private fun escapeCsv(value: String): String =
        if (value.contains(',') || value.contains('"') || value.contains('\n')) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
}
