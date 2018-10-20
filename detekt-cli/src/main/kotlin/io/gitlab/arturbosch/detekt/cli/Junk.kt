package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Finding
import java.util.HashMap

val Finding.baselineId: String
	get() = this.id + ":" + this.signature

const val SEPARATOR_COMMA = ","
const val SEPARATOR_SEMICOLON = ";"

inline fun <T, K, V> Collection<T>.toHashMap(
		keyFunction: (T) -> K,
		valueFunction: (T) -> V
): HashMap<K, V> {
	val result = HashMap<K, V>()
	for (element in this) {
		result[keyFunction(element)] = valueFunction(element)
	}
	return result
}
