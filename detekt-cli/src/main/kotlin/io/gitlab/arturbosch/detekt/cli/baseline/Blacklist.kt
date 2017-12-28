package io.gitlab.arturbosch.detekt.cli.baseline

/**
 * @author Artur Bosch
 */
data class Blacklist(override val ids: Set<String>,
					 override val timestamp: String) : Listing<Blacklist> {

	override fun withNewTimestamp(timestamp: String,
								  list: Blacklist) = Blacklist(list.ids, timestamp)

	override fun toString(): String {
		return "Blacklist(ids=$ids, timestamp='$timestamp')"
	}
}
