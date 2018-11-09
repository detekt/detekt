package io.gitlab.arturbosch.detekt.cli.baseline

/**
 * @author Artur Bosch
 * @author Markus Schwarz
 */
internal data class Baseline(val sourceSetId: String?, val blacklist: Blacklist, val whitelist: Whitelist)

internal data class ConsolidatedBaseline(val baselines: List<Baseline> = emptyList()) {
	fun withSourceSetId(sourceSetId: String?) = baselines.firstOrNull { it.sourceSetId == sourceSetId }
	fun contains(sourceSetId: String?) = baselines.any { it.sourceSetId == sourceSetId }

	fun addOrReplace(baseline: Baseline): ConsolidatedBaseline {

		return if (contains(baseline.sourceSetId))
			copy(baselines = baselines.filter { it.sourceSetId != baseline.sourceSetId } + baseline)
		else
			copy(baselines = baselines + baseline)
	}
}

internal const val SMELL_BASELINE_CONTAINER = "SmellBaselines"
internal const val SMELL_BASELINE = "SmellBaseline"
internal const val BLACKLIST = "Blacklist"
internal const val WHITELIST = "Whitelist"
internal const val TIMESTAMP = "timestamp"
internal const val ID = "ID"
internal const val SOURCE_SET_ID = "sourceSet"

class InvalidBaselineState(msg: String, error: Throwable? = null) : IllegalStateException(msg, error)
