package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Finding

data class Baseline(val blacklist: Blacklist, val whitelist: Whitelist)

const val SMELL_BASELINE = "SmellBaseline"
const val BLACKLIST = "Blacklist"
const val WHITELIST = "Whitelist"
const val ID = "ID"

val Finding.baselineId: String
    get() = this.id + ":" + this.signature
