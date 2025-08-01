package dev.detekt.api

/**
 * Convert a simple pattern String to a Regex
 *
 * The simple pattern is a subset of the shell pattern matching or
 * [glob][https://en.wikipedia.org/wiki/Glob_(programming)]
 *
 * '*' matches any zero or more characters
 * '?' matches any one character
 */
fun String.simplePatternToRegex(): Regex =
    this
        .replace(".", "\\.")
        .replace("*", ".*")
        .replace("?", ".")
        .toRegex()
