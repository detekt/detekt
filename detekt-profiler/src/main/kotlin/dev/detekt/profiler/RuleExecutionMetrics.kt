package dev.detekt.profiler

import dev.detekt.api.RuleSetId
import kotlin.time.Duration

/**
 * Keys for storing rule execution profiling data in [dev.detekt.api.Detektion.userData].
 */
object RuleProfilingKeys {
    /**
     * Key for storing aggregated per-rule metrics as `List<RuleExecutionMetric>`.
     */
    const val RULE_METRICS: String = "ruleExecutionMetrics"

    /**
     * Key for storing detailed per-rule-per-file executions as `List<RuleFileExecution>`.
     */
    const val FILE_EXECUTIONS: String = "ruleFileExecutions"

    /**
     * Key for storing whether parallel execution was disabled for profiling as `Boolean`.
     * When true, indicates that --parallel was requested but sequential execution was
     * used instead to ensure accurate timing measurements.
     */
    const val PARALLEL_DISABLED: String = "profilingParallelDisabled"
}

/**
 * Aggregated metrics for a single rule across all files.
 *
 * @property ruleId The unique identifier of the rule
 * @property ruleSetId The rule set containing this rule
 * @property totalDuration Total time spent executing this rule across all files
 * @property executionCount Number of files this rule was executed against
 * @property totalFindings Total number of findings reported by this rule
 */
data class RuleExecutionMetric(
    val ruleId: String,
    val ruleSetId: RuleSetId,
    val totalDuration: Duration,
    val executionCount: Int,
    val totalFindings: Int,
) {
    /**
     * Average duration per file for this rule.
     */
    val averageDuration: Duration
        get() = if (executionCount > 0) totalDuration / executionCount else Duration.ZERO
}

/**
 * Detailed execution data for a single rule processing a single file.
 *
 * @property ruleId The unique identifier of the rule
 * @property ruleSetId The rule set containing this rule
 * @property filePath The path of the file that was processed
 * @property duration Time taken by the rule to process this file
 * @property findings Number of findings reported for this file
 */
data class RuleFileExecution(
    val ruleId: String,
    val ruleSetId: RuleSetId,
    val filePath: String,
    val duration: Duration,
    val findings: Int,
)
