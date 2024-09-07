package io.gitlab.arturbosch.detekt.api

/**
 * Represents a detected problem in the source code.
 *
 * A finding has a source code position described as an entity and a message.
 * Entity references can also be considered for deeper characterization.
 */
interface Finding {
    val entity: Entity
    val references: List<Entity>
    val message: String
    val suppressReasons: List<String>
}
