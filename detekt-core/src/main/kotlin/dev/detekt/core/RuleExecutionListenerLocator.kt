package dev.detekt.core

import dev.detekt.api.RuleExecutionListener
import dev.detekt.core.extensions.loadExtensions

/**
 * Locates and loads [RuleExecutionListener] implementations.
 *
 * Listeners are loaded when `--profiling` flag is passed on the command line
 * or `profiling = true` is set in the Gradle plugin configuration.
 */
class RuleExecutionListenerLocator(private val settings: ProcessingSettings) {

    private val profilingActive = settings.spec.executionSpec.profiling

    fun load(): List<RuleExecutionListener> =
        if (profilingActive) {
            loadExtensions(settings)
        } else {
            emptyList()
        }
}
