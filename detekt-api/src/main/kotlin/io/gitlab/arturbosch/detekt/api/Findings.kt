package io.gitlab.arturbosch.detekt.api

/**
 * Represents a problem detected by a rule on the source code
 *
 * A finding has an issue (information about the rule that detected the problem), a severity and a source code position
 * described as an entity. Entity references can also be considered for deeper characterization.
 */
interface Finding : HasEntity {
    val issue: Issue
    val references: List<Entity>
    val message: String
    val severity: Severity
        get() = Severity.DEFAULT
}
