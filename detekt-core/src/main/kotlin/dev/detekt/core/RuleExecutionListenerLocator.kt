package dev.detekt.core

import dev.detekt.api.RuleExecutionListener
import dev.detekt.core.extensions.loadExtensions

/**
 * Locates and loads [RuleExecutionListener] implementations via ServiceLoader.
 *
 * Listeners are loaded when their implementations are present on the classpath.
 * For example, adding the `detekt-profiler` module enables profiling automatically.
 */
class RuleExecutionListenerLocator(private val settings: ProcessingSettings) {

    fun load(): List<RuleExecutionListener> = loadExtensions(settings)
}
