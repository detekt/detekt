package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

class ProjectStatisticsReport : ConsoleReport() {

    override val priority: Int = 10

    override fun render(detektion: Detektion): String? {
        val metrics = detektion.metrics
        if (metrics.isEmpty()) return null
        return with(StringBuilder()) {
            append("Project Statistics:".format())
            metrics.sortedBy { it.priority }
                    .reversed()
                    .forEach { append(it.toString().format(PREFIX)) }
            toString()
        }
    }
}
