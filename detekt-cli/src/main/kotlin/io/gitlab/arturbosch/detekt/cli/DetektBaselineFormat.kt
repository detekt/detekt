package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import io.gitlab.arturbosch.quide.format.BaselineFormat
import io.gitlab.arturbosch.quide.format.xml.Blacklist
import io.gitlab.arturbosch.quide.format.xml.SmellBaseline
import io.gitlab.arturbosch.quide.format.xml.Whitelist
import java.nio.file.Path
import java.time.Instant

/**
 * @author Artur Bosch
 */
object DetektBaselineFormat {

	const val BASELINE_FILE = "baseline.xml"

	private val baseline = BaselineFormat()

	fun create(smells: List<Finding>, path: Path) {
		val baselinePath = path.resolve(BASELINE_FILE)
		val timestamp = Instant.now().toEpochMilli().toString()
		val blacklist = if (baselinePath.exists() && baselinePath.isFile()) {
			loadFormat(baselinePath).blacklist
		} else {
			Blacklist(emptyList(), timestamp)
		}
		val ids = smells.map { it.id + ":" + it.signature }
		val smellBaseline = SmellBaseline(blacklist, Whitelist(ids, timestamp))
		baseline.write(smellBaseline, baselinePath)
	}

	private fun loadFormat(path: Path): SmellBaseline = baseline.read(path)

}