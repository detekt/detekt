package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config

/**
 * @author Artur Bosch
 */
object SmellBorder {

	class SmellThresholdReachedError(override val message: String?) : RuntimeException(message)

	fun check(amount: Int, config: Config) {
		val subConfig = config.subConfig("build")
		val warning = subConfig.valueOrDefault("warningThreshold") { -1 }
		val fail = subConfig.valueOrDefault("failThreshold") { -1 }

		println("\n")
		if (fail.reached(amount)) {
			throw SmellThresholdReachedError("Code smell threshold of $fail reached with $amount smells found!")
		} else if (warning.reached(amount)) {
			println("Warning: $amount code smells found. Warning threshold is $warning and fail threshold is $fail!")
		}

	}

	private fun Int.reached(amount: Int): Boolean = this != -1 && this <= amount

}
