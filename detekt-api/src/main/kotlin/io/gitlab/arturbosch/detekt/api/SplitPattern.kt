package io.gitlab.arturbosch.detekt.api

/**
 * Splits given text into parts and provides testing utilities for its elements.
 * Basic use cases are to specify different function or class names in the detekt
 * yaml config and test for their appearance in specific rules.
 */
class SplitPattern(
    text: String,
    delimiters: String = ",",
    removeTrailingAsterisks: Boolean = true
) {

    @Suppress("detekt.SpreadOperator")
    private val excludes = text
        .splitToSequence(*delimiters.toCharArray())
        .map { it.trim() }
        .filter { it.isNotBlank() }
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
     * Is there any element which matches given [value]?
     */
    fun equals(value: String?): Boolean = excludes.any { value?.equals(it, ignoreCase = true) == true }

    /**
     * Tests if none of the parts contain the given [value].
     */
    fun none(value: String): Boolean = !contains(value)

    /**
     * Finds all parts which match given [value].
     */
    fun matches(value: String): List<String> = excludes.filter { value.contains(it, ignoreCase = true) }

    /**
     * Tests if any part starts with given [value]
     */
    fun startWith(value: String?): Boolean = excludes.any { value?.startsWith(it) ?: false }

    /**
     * Transforms all parts by given [transform] function.
     */
    fun <T> mapAll(transform: (String) -> T): List<T> = excludes.map(transform)
}
