package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.baseline.Baseline
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFormat
import io.gitlab.arturbosch.detekt.cli.baseline.Blacklist
import io.gitlab.arturbosch.detekt.cli.baseline.Whitelist
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Path
import java.time.Instant

/**
 * @author Artur Bosch
 */
object DetektBaselineFormat {

	const val BASELINE_FILE = "baseline.xml"

	private val baseline = BaselineFormat

	fun fullPath(reportsPath: Path?): Path? = reportsPath?.resolve(BASELINE_FILE)

	fun create(smells: List<Finding>, path: Path) {
		val baselinePath = fullPath(path)!!
		val timestamp = Instant.now().toEpochMilli().toString()
		val blacklist = if (exist(baselinePath)) {
			load(baselinePath).blacklist
		} else {
			Blacklist(emptyList(), timestamp)
		}
		val ids = smells.map { it.baselineId }
		val smellBaseline = Baseline(blacklist, Whitelist(ids, timestamp))
		baseline.write(smellBaseline, baselinePath)
		println(" Successfully wrote smell baseline to $baselinePath")
	}

	fun load(path: Path): Baseline = baseline.read(path)

	fun listings(path: Path?): Pair<Whitelist, Blacklist>? {
		val baselinePath = fullPath(path)
		return if (DetektBaselineFormat.exist(baselinePath)) {
			val format = DetektBaselineFormat.load(baselinePath!!)
			format.whitelist to format.blacklist
		} else null
	}

	private fun exist(baselinePath: Path?) = baselinePath != null && baselinePath.exists() && baselinePath.isFile()

}

fun List<Finding>.filterListedFindings(listings: Pair<Whitelist, Blacklist>?): List<Finding> {
	return if (listings != null) {
		val whiteFiltered = this.filterNot { finding -> listings.first.ids.contains(finding.baselineId) }
		val blackFiltered = whiteFiltered.filterNot { finding -> listings.second.ids.contains(finding.baselineId) }
		blackFiltered
	} else this
}