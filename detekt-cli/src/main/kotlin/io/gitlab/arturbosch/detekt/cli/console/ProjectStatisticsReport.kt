package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.PREFIX
import io.gitlab.arturbosch.detekt.api.format

/**
 * @author Artur Bosch
 */
class ProjectStatisticsReport : ConsoleReport() {

	override val priority: Int = 1

	override fun render(detektion: Detektion): String? {
		val metrics = detektion.metrics
		if (metrics.isEmpty()) return null
		return with(StringBuilder()) {
			append("Project Statistics:".format())
			metrics.forEach { append(it.toString().format(PREFIX)) }
			toString()
		}
	}
}
