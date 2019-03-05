package io.gitlab.arturbosch.detekt.cli.baseline

/**
 * @author Artur Bosch
 */
data class Baseline(val blacklist: Blacklist, val whitelist: Whitelist) {

    override fun toString(): String {
        return "Baseline(blacklist=$blacklist, whitelist=$whitelist)"
    }
}

const val SMELL_BASELINE = "SmellBaseline"
const val BLACKLIST = "Blacklist"
const val WHITELIST = "Whitelist"
const val ID = "ID"

class InvalidBaselineState(msg: String, error: Throwable) : IllegalStateException(msg, error)
