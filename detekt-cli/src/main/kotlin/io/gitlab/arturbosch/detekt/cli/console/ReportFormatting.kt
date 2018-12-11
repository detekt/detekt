package io.gitlab.arturbosch.detekt.cli.console

/**
 * @author Artur Bosch
 */

const val PREFIX = "\t- "

fun Any.format(prefix: String = "", suffix: String = "\n") = "$prefix$this$suffix"
