package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Finding

/**
 * @author Artur Bosch
 */

inline fun <T> Collection<T>.each(action: (T) -> Unit) {
	for (element in this) action(element)
}

fun Any?.print(prefix: String = "", suffix: String = "") {
	println("$prefix$this$suffix")
}

fun printFindings(result: Map<String, List<Finding>>) {
	result.forEach {
		it.key.print("Ruleset: ")
		it.value.each { it.compact().print("\t") }
	}
}