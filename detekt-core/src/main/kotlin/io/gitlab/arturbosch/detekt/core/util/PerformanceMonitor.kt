package io.gitlab.arturbosch.detekt.core.util

import io.gitlab.arturbosch.detekt.api.PropertiesAware
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.getOrNull
import kotlin.time.Duration
import kotlin.time.measureTimedValue

class PerformanceMonitor {

    enum class Phase {
        LoadConfig,
        CreateSettings,
        ValidateConfig,
        Parsing,
        Binding,
        LoadingExtensions,
        Analyzer,
        Reporting,
    }

    data class Entry(val phase: Phase, val duration: Duration)

    private val finished: MutableList<Entry> = mutableListOf()

    fun allFinished(): List<Entry> = finished

    fun <R> measure(phase: Phase, block: () -> R): R {
        val timedBlockExecution = measureTimedValue { block() }
        finished.add(Entry(phase, timedBlockExecution.duration))
        return timedBlockExecution.value
    }
}

internal const val MONITOR_PROPERTY_KEY = "detekt.core.monitor"

@OptIn(UnstableApi::class)
internal fun PropertiesAware.getOrCreateMonitor() =
    getOrNull(MONITOR_PROPERTY_KEY) ?: PerformanceMonitor().also { register(MONITOR_PROPERTY_KEY, it) }
