package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

class ProjectStatisticsReport : ConsoleReport() {

    override val priority: Int = 10

    override fun render(detektion: Detektion): String? {
        val metrics = detektion.metrics
        if (metrics.isEmpty()) return null
        return with(StringBuilder()) {
            append("Project Statistics:\n")
            metrics.sortedBy { -it.priority }
                .forEach {
                    append("\t- ")
                    append(it)
                    append("\n")
                }
            toString()
        }
    }
}
