package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.PREFIX
import io.gitlab.arturbosch.detekt.api.format

internal class ComplexityReportGenerator(private val complexityMetric: ComplexityMetric) {

	private var numberOfSmells = 0
	private var smellPerThousandLines = 0
	private var mccPerThousandLines = 0
	private var commentSourceRatio = 0

	companion object Factory {
		fun create(detektion: Detektion): ComplexityReportGenerator = ComplexityReportGenerator(ComplexityMetric(detektion))
	}

	fun generate(): String? {
		if (cannotGenerate()) return null
		return with(StringBuilder()) {
			append("Complexity Report:".format())
			append("${complexityMetric.loc} lines of code (loc)".format(PREFIX))
			append("${complexityMetric.sloc} source lines of code (sloc)".format(PREFIX))
			append("${complexityMetric.lloc} logical lines of code (lloc)".format(PREFIX))
			append("${complexityMetric.cloc} comment lines of code (cloc)".format(PREFIX))
			append("${complexityMetric.mcc} McCabe complexity (mcc)".format(PREFIX))
			append("$numberOfSmells number of total code smells".format(PREFIX))
			append("$commentSourceRatio % comment source ratio".format(PREFIX))
			append("$mccPerThousandLines mcc per 1000 lloc".format(PREFIX))
			append("$smellPerThousandLines code smells per 1000 lloc".format(PREFIX))
			toString()
		}
	}

	private fun cannotGenerate(): Boolean {
		return when {
			complexityMetric.mcc == null -> true
			complexityMetric.lloc == null || complexityMetric.lloc == 0 -> true
			complexityMetric.sloc == null || complexityMetric.sloc == 0 -> true
			complexityMetric.cloc == null -> true
			else -> {
				numberOfSmells = complexityMetric.findings.sumBy { it.value.size }
				smellPerThousandLines = numberOfSmells * 1000 / complexityMetric.lloc
				mccPerThousandLines = complexityMetric.mcc * 1000 / complexityMetric.lloc
				commentSourceRatio = complexityMetric.cloc * 100 / complexityMetric.sloc
				false
			}
		}
	}
}
