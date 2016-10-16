package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.api.Finding

/**
 * @author Artur Bosch
 */

inline fun <T> Collection<T>.each(action: (T) -> Unit): Unit {
	for (element in this) action(element)
}

fun Any?.print(prefix: String = "", suffix: String = ""): Unit {
	println("$prefix$this$suffix")
}

fun printFindings(result: Pair<String, List<Finding>>) {
	result.first.print("Ruleset: ")
	result.second.each { it.compact().print("\t") }
}