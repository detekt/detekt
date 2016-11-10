package io.gitlab.arturbosch.detekt.cli

/**
 * @author Artur Bosch
 */

inline fun <T> Collection<T>.each(action: (T) -> Unit) {
	for (element in this) action(element)
}

fun Any?.print(prefix: String = "", suffix: String = "") {
	println("$prefix$this$suffix")
}
