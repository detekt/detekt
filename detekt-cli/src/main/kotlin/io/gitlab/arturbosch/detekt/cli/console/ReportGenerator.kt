package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.PREFIX
import io.gitlab.arturbosch.detekt.api.format

internal class ReportGenerator(private val metric: Metric) {

	private var numberOfSmells = 0
	private var smellPerThousandLines = 0
	private var mccPerThousandLines = 0
	private var commentSourceRatio = 0

	companion object Factory {
		fun create(detektion: Detektion): ReportGenerator = ReportGenerator(Metric(detektion))
		const val ONE_THOUSAND = 1000
	}

	fun generate(): String? {
		if (cannotGenerate()) return null
		return with(StringBuilder()) {
			append("Complexity Report:".format())
			append("${metric.loc} lines of code (loc)".format(PREFIX))
			append("${metric.sloc} source lines of code (sloc)".format(PREFIX))
			append("${metric.lloc} logical lines of code (lloc)".format(PREFIX))
			append("${metric.cloc} comment lines of code (cloc)".format(PREFIX))
			append("${metric.mcc} McCabe complexity (mcc)".format(PREFIX))
			append("$numberOfSmells number of total code smells".format(PREFIX))
			append("$commentSourceRatio comment source ratio".format(PREFIX))
			append("$mccPerThousandLines mcc per 1000 lloc".format(PREFIX))
			append("$smellPerThousandLines code smells per 1000 lloc".format(PREFIX))
			toString()
		}
	}

	private fun cannotGenerate(): Boolean {
		return when {
			metric.mcc == null -> true
			metric.lloc == null || metric.lloc == 0 -> true
			metric.sloc == null || metric.sloc == 0 -> true
			metric.cloc == null -> true
			else -> {
				numberOfSmells = metric.findings.sumBy { it.value.size }
				smellPerThousandLines = numberOfSmells * ONE_THOUSAND / metric.lloc
				mccPerThousandLines = metric.mcc * ONE_THOUSAND / metric.lloc
				commentSourceRatio = metric.cloc * 100 / metric.sloc
				false
			}
		}
	}
}
