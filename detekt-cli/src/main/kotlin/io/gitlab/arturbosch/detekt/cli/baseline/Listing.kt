package io.gitlab.arturbosch.detekt.cli.baseline

import java.time.Instant

/**
 * @author Artur Bosch
 */
interface Listing<T> {

	val timestamp: String
	val ids: Set<String>

	fun isOlderThan(instant: Instant): Boolean {
		val timeMillis = Instant.ofEpochMilli(timestamp.toLong())
		return timeMillis < instant
	}

	fun withNewTimestamp(timestamp: String, list: T): T
}
