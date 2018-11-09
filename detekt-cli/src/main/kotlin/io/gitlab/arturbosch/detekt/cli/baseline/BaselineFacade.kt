package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.baselineId
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant

/**
 * @author Artur Bosch
 */
class BaselineFacade(val baselineFile: Path, private val sourceSetId: String? = null) {

	private val listings: Pair<Whitelist, Blacklist>? =
			if (baselineExists()) {
				val format = BaselineFormat().read(baselineFile, sourceSetId)
				format.whitelist to format.blacklist
			} else null

	fun filter(smells: List<Finding>) =
			if (listings != null) {
				val whiteFiltered = smells.filterNot { finding -> listings.first.ids.contains(finding.baselineId) }
				val blackFiltered = whiteFiltered.filterNot { finding -> listings.second.ids.contains(finding.baselineId) }
				blackFiltered
			} else smells

	fun create(smells: List<Finding>) {
		val timestamp = Instant.now().toEpochMilli().toString()
		val existingOrNew = if (baselineExists()) {
			BaselineFormat().readConsolidated(baselineFile)
		} else {
			ConsolidatedBaseline()
		}
		val ids = smells.map(Finding::baselineId).toSortedSet()
		val smellWhitelist = Whitelist(ids, timestamp)
		val smellBaseline = existingOrNew.withSourceSetId(sourceSetId)?.copy(whitelist = smellWhitelist)
				?: Baseline(sourceSetId, Blacklist(emptySet(), timestamp), smellWhitelist)
		val consolidatedResult = existingOrNew.addOrReplace(smellBaseline)

		baselineFile.parent?.let { Files.createDirectories(it) }
		BaselineFormat().write(consolidatedResult, baselineFile)
		println("Successfully wrote smell baseline to $baselineFile")
	}

	private fun baselineExists() = baselineFile.exists() && baselineFile.isFile()
}
