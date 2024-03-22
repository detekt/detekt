package io.gitlab.arturbosch.detekt.api

/**
 * Represents a problem in the source code detected by a rule.
 *
 * A finding has an issue (information about the rule that detected the problem), a severity and a source code position.
 * described as an entity. Entity references can also be considered for deeper characterization.
 */
interface Finding {
    val entity: Entity
    val references: List<Entity>
    val message: String
}
