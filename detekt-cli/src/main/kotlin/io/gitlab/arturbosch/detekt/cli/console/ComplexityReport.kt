package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.PREFIX
import io.gitlab.arturbosch.detekt.api.format
import io.gitlab.arturbosch.detekt.core.processors.COMPLEXITY_KEY
import io.gitlab.arturbosch.detekt.core.processors.LLOC_KEY
import io.gitlab.arturbosch.detekt.core.processors.NUMBER_OF_COMMENT_LINES_KEY

/**
 * @author Artur Bosch
 */
class ComplexityReport : ConsoleReport() {

	override val priority: Int = 20

	override fun render(detektion: Detektion): String? {
		val findings = detektion.findings
		val mcc = detektion.getData(COMPLEXITY_KEY)
		val lloc = detektion.getData(LLOC_KEY)
		val cloc = detektion.getData(NUMBER_OF_COMMENT_LINES_KEY)
		if (mcc != null && lloc != null && lloc > 0) {
			val numberOfSmells = findings.entries.sumBy { it.value.size }
			val smellPerThousandLines = numberOfSmells * 1000 / lloc
			val mccPerThousandLines = mcc * 1000 / lloc
			return with(StringBuilder()) {
				append("Complexity Report:".format())
				append("$lloc logical lines of code (lloc)".format(PREFIX))
				append("$cloc comment lines of code (cloc)".format(PREFIX))
				append("$mcc McCabe complexity (mcc)".format(PREFIX))
				append("$numberOfSmells number of total code smells".format(PREFIX))
				append("$mccPerThousandLines mcc per 1000 lloc".format(PREFIX))
				append("$smellPerThousandLines code smells per 1000 lloc".format(PREFIX))
				toString()
			}
		} else {
			return null
		}
	}

}
