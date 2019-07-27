package io.gitlab.arturbosch.detekt.cli.console

const val PREFIX = "\t- "

fun Any.format(prefix: String = "", suffix: String = "\n") = "$prefix$this$suffix"
