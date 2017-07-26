package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Finding

val Finding.baselineId: String
	get() = this.id + ":" + this.signature

val SEPARATOR_COMMA = ","
val SEPARATOR_SEMICOLON = ";"
