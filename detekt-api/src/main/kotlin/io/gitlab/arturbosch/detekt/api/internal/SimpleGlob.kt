package io.gitlab.arturbosch.detekt.api.internal

/**
 * This simple globbing implementation allows users to define patterns with `?` (any single character)
 * and `*` (zero or more characters) wildcards.
 */
class SimpleGlob private constructor(private val regex: Regex) {

    fun matches(input: String) = regex.matches(input)

    companion object {
        fun of(globPattern: String): SimpleGlob {
            val regex = globPattern
                .replace("\\", "\\\\")
                .replace(".", "\\.")
                .replace("*", ".*")
                .replace("?", ".")
                .toRegex()
            return SimpleGlob(regex)
        }
    }
}
