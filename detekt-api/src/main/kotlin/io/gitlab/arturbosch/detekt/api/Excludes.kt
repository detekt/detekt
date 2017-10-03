package io.gitlab.arturbosch.detekt.api

class Excludes(excludeParameter: String) {
	private val excludes = excludeParameter
			.split(",")
			.map { it.trim() }
			.filter { it.isNotBlank() }
			.map { it.removeSuffix("*") }

	fun contains(value: String?) = excludes.any { value?.contains(it, ignoreCase = true) == true }
	fun none(value: String) = !contains(value)
	fun matches(value: String): List<String> = excludes.filter { value.contains(it, ignoreCase = true) }
	fun startWith(name: String?) = excludes.any { name?.startsWith(it) ?: false }
}
