package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.SplitPattern

class CommaSeparatedPattern(
    text: String,
    delimiters: String = ","
) : SplitPattern(text, delimiters, false) {

    fun mapToRegex(): Set<Regex> = mapAll { it.toRegex() }.toSet()
}
