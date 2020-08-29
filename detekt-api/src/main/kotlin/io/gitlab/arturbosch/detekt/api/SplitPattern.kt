package io.gitlab.arturbosch.detekt.api

/**
 * Splits given text into parts and provides testing utilities for its elements.
 * Basic use cases are to specify different function or class names in the detekt
 * yaml config and test for their appearance in specific rules.
 */
open class SplitPattern(
    text: String,
    delimiters: String = ",",
    removeTrailingAsterisks: Boolean = true
) {

    @Suppress("detekt.SpreadOperator")
    private val excludes = text
        .commaSeparatedPattern(*delimiters.toCharArray().map { it.toString() }.toTypedArray())
        .mapIf(removeTrailingAsterisks) { seq ->
            seq.map { it.removePrefix("*") }
                .map { it.removeSuffix("*") }
        }.toList()

    private fun <T> Sequence<T>.mapIf(
        condition: Boolean,
        then: (Sequence<T>) -> Sequence<T>
    ): Sequence<T> = if (condition) then(this) else this

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

    /**
     * Transforms all parts by given [transform] function.
     */
    fun <T> mapAll(transform: (String) -> T): List<T> = excludes.map(transform)
}

/**
 * Splits given String into a sequence of strings splited by the provided delimiters ("," by default).
 *
 * It also trims the strings and removes the empty ones
 */
@Suppress("detekt.SpreadOperator")
fun String.commaSeparatedPattern(vararg delimiters: String = arrayOf(",")): Sequence<String> {
    return this
        .splitToSequence(*delimiters)
        .filter { it.isNotBlank() }
        .map { it.trim() }
}

/**
 * Convert a simple pattern String to a Regex
 *
 * The simple pattern is a subset of the shell pattern matching or
 * [glob][https://en.wikipedia.org/wiki/Glob_(programming)]
 *
 * '*' matches any zero or more characters
 * '?' matches any one character
 */
fun String.simplePatternToRegex(): Regex {
    return this
        .replace(".", "\\.")
        .replace("*", ".*")
        .replace("?", ".?")
        .toRegex()
}
