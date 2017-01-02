package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.Detektion

/**
 * @author Artur Bosch
 */
class SmellBorder(config: Config, main: Main) {

	private val subConfig = config.subConfig("build")
	private val warning = subConfig.valueOrDefault("warningThreshold") { -1 }
	private val fail = subConfig.valueOrDefault("failThreshold") { -1 }
	private val reportDirectory = main.reportDirectory

	class SmellThresholdReachedError(override val message: String?) : RuntimeException(message)

	fun check(detektion: Detektion) {
		val listings = DetektBaselineFormat.listings(reportDirectory)
		val smells = detektion.findings.flatMap { it.value }
		val filteredSmells = smells.filterListedFindings(listings)
		val amount = filteredSmells.size

		println("\n")
		if (fail.reached(amount)) {
			throw SmellThresholdReachedError("Code smell threshold of $fail reached with $amount smells found!")
		} else if (warning.reached(amount)) {
			println("Warning: $amount code smells found. Warning threshold is $warning and fail threshold is $fail!")
		}

	}

	private fun Int.reached(amount: Int): Boolean = this != -1 && this <= amount

}
