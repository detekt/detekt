package io.gitlab.arturbosch.detekt.cli.baseline

/**
 * @author Artur Bosch
 */
data class Baseline(val blacklist: Blacklist, val whitelist: Whitelist) {

	override fun toString(): String {
		return "Baseline(blacklist=$blacklist, whitelist=$whitelist)"
	}
}

val SMELL_BASELINE = "SmellBaseline"
val BLACKLIST = "Whitelist"
val WHITELIST = "Blacklist"
val TIMESTAMP = "timestamp"
val ID = "ID"

class InvalidBaselineState(msg: String, error: Throwable) : IllegalStateException(msg, error)
