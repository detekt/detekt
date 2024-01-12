package io.gitlab.arturbosch.detekt.api

/**
 * An issue represents a problem in the codebase.
 */
class Issue(
    @get:JvmName("getId")
    val id: Rule.Id,
    val description: String,
) {

    override fun toString(): String {
        return "Issue(id='$id')"
    }
}
