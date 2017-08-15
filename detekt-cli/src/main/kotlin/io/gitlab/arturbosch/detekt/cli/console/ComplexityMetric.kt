package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.processors.COMPLEXITY_KEY
import io.gitlab.arturbosch.detekt.core.processors.LLOC_KEY
import io.gitlab.arturbosch.detekt.core.processors.LOC_KEY
import io.gitlab.arturbosch.detekt.core.processors.NUMBER_OF_COMMENT_LINES_KEY
import io.gitlab.arturbosch.detekt.core.processors.SLOC_KEY

internal class ComplexityMetric(detektion: Detektion) {

	val mcc = detektion.getData(COMPLEXITY_KEY)
	val loc = detektion.getData(LOC_KEY)
	val sloc = detektion.getData(SLOC_KEY)
	val lloc = detektion.getData(LLOC_KEY)
	val cloc = detektion.getData(NUMBER_OF_COMMENT_LINES_KEY)
	val findings = detektion.findings.entries
}
