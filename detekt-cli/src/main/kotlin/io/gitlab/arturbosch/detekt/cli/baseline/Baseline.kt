package io.gitlab.arturbosch.detekt.cli.baseline

/**
 * @author Artur Bosch
 * @author Markus Schwarz
 */
internal data class Baseline(val blacklist: Blacklist, val whitelist: Whitelist)

internal data class ConsolidatedBaseline(
		val blacklist: Blacklist,
		val defaultWhitelist: Whitelist? = null,
		val whitelists: Map<String, Whitelist> = emptyMap()
)

internal const val SMELL_BASELINE = "SmellBaseline"
internal const val BLACKLIST = "Blacklist"
internal const val WHITELIST = "Whitelist"
internal const val TIMESTAMP = "timestamp"
internal const val ID = "ID"
internal const val SOURCE_SET_ID = "sourceSet"

class InvalidBaselineState(msg: String, error: Throwable? = null) : IllegalStateException(msg, error)
