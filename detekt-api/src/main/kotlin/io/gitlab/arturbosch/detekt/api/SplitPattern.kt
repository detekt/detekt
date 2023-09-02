package io.gitlab.arturbosch.detekt.api

/**
 * Splits given String into a sequence of strings split by the provided delimiters ("," by default).
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
        .replace("?", ".")
        .toRegex()
}
