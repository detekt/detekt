package dev.detekt.api

import org.jetbrains.kotlin.psi.KtFile
import kotlin.time.Duration

/**
 * Listener for observing individual rule executions during analysis.
 * Enables profiling, monitoring, and custom metric collection for rule execution.
 *
 * Implementations must be thread-safe as methods may be called concurrently from multiple threads.
 * Pay attention to the thread policy of each function!
 *
 * Listeners are loaded via ServiceLoader when their implementations are on the classpath.
 * For example, adding the `detekt-profiler` module to `detektPlugins` (Gradle)
 * or `--plugins path/to/detekt-profiler.jar` (CLI) enables profiling.
 */
@Suppress("EmptyFunctionBlock")
interface RuleExecutionListener : Extension {

    /**
     * Use this to prepare for analysis and capture the scope of work.
     * This calculation should be lightweight as this method is called from the main thread.
     */
    fun onStart(files: List<KtFile>, rules: List<RuleInstance>) {}

    /**
     * Called before a rule processes a file.
     * This method is called from a thread pool thread. Heavy computations allowed.
     */
    fun beforeRuleExecution(file: KtFile, rule: RuleInstance) {}

    /**
     * Called after a rule processes a file.
     * This method is called from a thread pool thread. Heavy computations allowed.
     */
    fun afterRuleExecution(file: KtFile, rule: RuleInstance, findings: Int, duration: Duration) {}

    /**
     * Mainly use this method to save computed metrics to the [Detektion] container.
     * Do not do heavy computations here as this method is called from the main thread.
     * Results can be stored in [Detektion.userData] for access by reporting extensions.
     *
     * This method is called before any [ReportingExtension].
     */
    fun onFinish(result: Detektion): Detektion = result
}
