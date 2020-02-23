package io.gitlab.arturbosch.detekt.api.internal

private val identifierRegex = Regex("[aA-zZ]+([-][aA-zZ]+)*")

/**
 * Checks if given string matches the criteria of an id - [aA-zZ]+([-][aA-zZ]+)* .
 */
internal fun validateIdentifier(id: String) {

    require(id.matches(identifierRegex)) {
        "id '$id' must match ${identifierRegex.pattern}"
    }
}
