package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.processors.complexityKey
import io.gitlab.arturbosch.detekt.core.processors.logicalLinesKey
import io.gitlab.arturbosch.detekt.core.processors.linesKey
import io.gitlab.arturbosch.detekt.core.processors.commentLinesKey
import io.gitlab.arturbosch.detekt.core.processors.sourceLinesKey

internal class ComplexityMetric(detektion: Detektion) {

	val mcc = detektion.getData(complexityKey)
	val loc = detektion.getData(linesKey)
	val sloc = detektion.getData(sourceLinesKey)
	val lloc = detektion.getData(logicalLinesKey)
	val cloc = detektion.getData(commentLinesKey)
	val findings = detektion.findings.entries
}
