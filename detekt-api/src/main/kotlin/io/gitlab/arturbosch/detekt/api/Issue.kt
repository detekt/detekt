package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.validateIdentifier

/**
 * An issue represents a problem in the codebase.
 */
class Issue(
    val id: String,
    val description: String,
) {

    init {
        validateIdentifier(id)
    }

    override fun toString(): String {
        return "Issue(id='$id')"
    }
}
