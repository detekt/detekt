package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.commaSeparatedPattern

class CommaSeparatedPattern(
    text: String,
    delimiters: String = ","
) {

    @Suppress("detekt.SpreadOperator")
    private val excludes = text
        .commaSeparatedPattern(*delimiters.toCharArray().map { it.toString() }.toTypedArray())
        .toList()

    fun mapToRegex(): Set<Regex> = excludes.map { it.toRegex() }.toSet()

    /**
     * Does any part contain given [value]?
     */
    fun contains(value: String?): Boolean = excludes.any { value?.contains(it, ignoreCase = true) == true }

    /**
     * Is there any element which matches the given [value]?
     */
    fun any(value: String?): Boolean = excludes.any { value?.equals(it, ignoreCase = true) == true }

    /**
     * Tests if none of the parts contain the given [value].
     */
    fun none(value: String): Boolean = !contains(value)

    /**
     * Finds all parts which match the given [value].
     */
    fun matches(value: String): List<String> = excludes.filter { value.contains(it, ignoreCase = true) }

    /**
     * Tests if any part starts with the given [value]
     */
    fun startWith(value: String?): Boolean = excludes.any { value?.startsWith(it) ?: false }
}
