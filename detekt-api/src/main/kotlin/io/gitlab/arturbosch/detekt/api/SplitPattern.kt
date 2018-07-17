package io.gitlab.arturbosch.detekt.api

private val regex = Regex(",")

class SplitPattern(text: String,
				   delimiters: Regex = regex) {

	private val excludes = text
			.split(delimiters)
			.map { it.trim() }
			.filter { it.isNotBlank() }
			.map { it.removePrefix("*") }
			.map { it.removeSuffix("*") }

	fun contains(value: String?) = excludes.any { value?.contains(it, ignoreCase = true) == true }
	fun equals(value: String?) = excludes.any { value?.equals(it, ignoreCase = true) == true }
	fun none(value: String) = !contains(value)
	fun matches(value: String): List<String> = excludes.filter { value.contains(it, ignoreCase = true) }
	fun startWith(name: String?) = excludes.any { name?.startsWith(it) ?: false }
	fun <T> mapAll(transform: (String) -> T) = excludes.map(transform)
}
