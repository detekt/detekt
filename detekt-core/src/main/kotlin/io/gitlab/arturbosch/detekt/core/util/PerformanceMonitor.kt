package io.gitlab.arturbosch.detekt.core.util

import io.gitlab.arturbosch.detekt.api.PropertiesAware
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.getOrNull
import java.time.Duration
import java.util.EnumMap
import java.util.LinkedList

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

    private val finished: MutableList<Entry> = LinkedList()
    private val started: MutableMap<Phase, StartMillis> = EnumMap(Phase::class.java)

    fun allFinished(): List<Entry> = finished

    fun start(phase: Phase) {
        started[phase] = System.currentTimeMillis()
    }

    fun finish(phase: Phase) {
        val start = requireNotNull(started[phase])
        val end = System.currentTimeMillis()
        finished.add(Entry(phase, Duration.ofMillis(end - start)))
    }

    fun <R> measure(phase: Phase, block: () -> R): R {
        start(phase)
        val result = block()
        finish(phase)
        return result
    }
}

typealias StartMillis = Long

internal const val MONITOR_PROPERTY_KEY = "detekt.core.monitor"

@OptIn(UnstableApi::class)
internal fun PropertiesAware.getOrCreateMonitor(): PerformanceMonitor {
    var monitor = getOrNull<PerformanceMonitor>(MONITOR_PROPERTY_KEY)
    if (monitor == null) {
        monitor = PerformanceMonitor()
        register(MONITOR_PROPERTY_KEY, monitor)
    }
    return monitor
}
