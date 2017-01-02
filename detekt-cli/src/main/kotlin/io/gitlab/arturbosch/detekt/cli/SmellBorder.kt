package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.Detektion

/**
 * @author Artur Bosch
 */
class SmellBorder(config: Config) {

	private val subConfig = config.subConfig("build")
	private val warning = subConfig.valueOrDefault("warningThreshold") { -1 }
	private val fail = subConfig.valueOrDefault("failThreshold") { -1 }

	class SmellThresholdReachedError(override val message: String?) : RuntimeException(message)

	fun check(detektion: Detektion) {
		val amount = detektion.findings.flatMap { it.value }.size

		println("\n")
		if (fail.reached(amount)) {
			throw SmellThresholdReachedError("Code smell threshold of $fail reached with $amount smells found!")
		} else if (warning.reached(amount)) {
			println("Warning: $amount code smells found. Warning threshold is $warning and fail threshold is $fail!")
		}

	}

	private fun Int.reached(amount: Int): Boolean = this != -1 && this <= amount

}
