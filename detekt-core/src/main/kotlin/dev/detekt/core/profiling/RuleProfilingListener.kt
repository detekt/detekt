package dev.detekt.core.profiling

import dev.detekt.api.Detektion
import dev.detekt.api.RuleExecutionListener
import dev.detekt.api.RuleExecutionMetric
import dev.detekt.api.RuleFileExecution
import dev.detekt.api.RuleInstance
import dev.detekt.api.RuleProfilingKeys
import dev.detekt.api.RuleSetId
import dev.detekt.psi.absolutePath
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration

/**
 * Built-in rule execution listener that collects profiling metrics.
 *
 * This listener accumulates per-rule-per-file execution data in a thread-safe manner
 * and aggregates the results in [onFinish], storing them in [Detektion.userData].
 *
 * The collected data can be accessed using [RuleProfilingKeys.RULE_METRICS] and
 * [RuleProfilingKeys.FILE_EXECUTIONS] keys.
 */
class RuleProfilingListener : RuleExecutionListener {

    override val id: String = "RuleProfilingListener"
    override val priority: Int = 0

    private data class ExecutionKey(val ruleId: String, val ruleSetId: RuleSetId, val filePath: String)

    private data class ExecutionData(val duration: Duration, val findings: Int)

    private val executions = ConcurrentHashMap<ExecutionKey, ExecutionData>()

    override fun afterRuleExecution(file: KtFile, rule: RuleInstance, findings: Int, duration: Duration) {
        val key = ExecutionKey(rule.id, rule.ruleSetId, file.absolutePath().toString())
        executions[key] = ExecutionData(duration, findings)
    }

    override fun onFinish(result: Detektion): Detektion {
        val fileExecutions = executions.map { (key, data) ->
            RuleFileExecution(
                ruleId = key.ruleId,
                ruleSetId = key.ruleSetId,
                filePath = key.filePath,
                duration = data.duration,
                findings = data.findings,
            )
        }

        val ruleMetrics = fileExecutions
            .groupBy { it.ruleId to it.ruleSetId }
            .map { (ruleKey, executions) ->
                val (ruleId, ruleSetId) = ruleKey
                RuleExecutionMetric(
                    ruleId = ruleId,
                    ruleSetId = ruleSetId,
                    totalDuration = executions.fold(Duration.ZERO) { acc, e -> acc + e.duration },
                    executionCount = executions.size,
                    totalFindings = executions.sumOf { it.findings },
                )
            }
            .sortedByDescending { it.totalDuration }

        result.userData[RuleProfilingKeys.RULE_METRICS] = ruleMetrics
        result.userData[RuleProfilingKeys.FILE_EXECUTIONS] = fileExecutions

        return result
    }
}
