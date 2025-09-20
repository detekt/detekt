package dev.detekt.core.util

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
