package dev.detekt.psi

/**
 * Convert a simple path pattern String to a Regex
 *
 * The simple pattern is a subset of the shell pattern matching or
 * [glob][https://en.wikipedia.org/wiki/Glob_(programming)]
 *
 * '*' matches any zero or more characters
 * '?' matches any one character
 */
fun String.pathGlobToRegex(): Regex =
    this
        .replace(".", "\\.")
        .replace("*", ".*")
        .replace("?", ".")
        .toRegex()

fun String.fullyQualifiedNameGlobToRegex(): Regex =
    this
        .replace(".", """\.""")
        .replace("**", "//")
        .replace("*", "[^.]*")
        .replace("?", "[^.]")
        .replace("//", """.*(?=\.)""")
        .toRegex()
