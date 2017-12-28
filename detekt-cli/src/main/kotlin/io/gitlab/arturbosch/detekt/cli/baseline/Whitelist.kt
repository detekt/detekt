package io.gitlab.arturbosch.detekt.cli.baseline

/**
 * @author Artur Bosch
 */
data class Whitelist(override val ids: Set<String>,
					 override val timestamp: String) : Listing<Whitelist> {

	override fun withNewTimestamp(timestamp: String,
								  list: Whitelist) = Whitelist(list.ids, timestamp)

	override fun toString(): String {
		return "Blacklist(ids=$ids, timestamp='$timestamp')"
	}
}
