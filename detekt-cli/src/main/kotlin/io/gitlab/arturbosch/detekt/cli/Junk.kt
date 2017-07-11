package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Finding

fun Any?.print(prefix: String = "", suffix: String = "") {
	println("$prefix$this$suffix")
}

val Finding.baselineId: String
	get() = this.id + ":" + this.signature

val SEPARATORS = arrayOf(",", ";")
