package io.gitlab.arturbosch.detekt.api

private val regex = Regex(",")

/**
 * Splits given text into parts and provides testing utilities for its elements.
 * Basic use cases are to specify different function or class names in the detekt
 * yaml config and test for their appearance in specific rules.
 */
class SplitPattern(text: String,
				   delimiters: Regex = regex) {

	private val excludes = text
			.split(delimiters)
			.map { it.trim() }
			.filter { it.isNotBlank() }
			.map { it.removePrefix("*") }
			.map { it.removeSuffix("*") }

	fun contains(value: String?): Boolean = excludes.any { value?.contains(it, ignoreCase = true) == true }
	fun equals(value: String?): Boolean = excludes.any { value?.equals(it, ignoreCase = true) == true }
	fun none(value: String): Boolean = !contains(value)
	fun matches(value: String): List<String> = excludes.filter { value.contains(it, ignoreCase = true) }
	fun startWith(name: String?): Boolean = excludes.any { name?.startsWith(it) ?: false }
	fun <T> mapAll(transform: (String) -> T): List<T> = excludes.map(transform)
}
