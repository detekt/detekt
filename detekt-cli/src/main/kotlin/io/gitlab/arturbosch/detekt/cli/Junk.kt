package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Finding

val Finding.baselineId: String
	get() = this.id + ":" + this.signature

const val SEPARATOR_COMMA = ","
const val SEPARATOR_SEMICOLON = ";"
