package io.gitlab.arturbosch.detekt.cli

/**
 * @author Artur Bosch
 */

fun Any?.print(prefix: String = "", suffix: String = "") {
	println("$prefix$this$suffix")
}
