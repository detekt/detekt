package io.gitlab.arturbosch.detekt.cli.baseline

import java.time.Instant

/**
 * @author Artur Bosch
 */
interface Listing<T> {

	val timestamp: String
	val ids: Set<String>

	fun withNewTimestamp(timestamp: String, list: T): T
}
