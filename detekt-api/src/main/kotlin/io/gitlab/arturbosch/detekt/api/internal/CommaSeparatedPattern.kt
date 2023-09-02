package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.commaSeparatedPattern

class CommaSeparatedPattern(text: String) {

    private val excludes = text
        .commaSeparatedPattern(",")
        .toList()

    fun mapToRegex(): Set<Regex> = excludes.map { it.toRegex() }.toSet()
}
