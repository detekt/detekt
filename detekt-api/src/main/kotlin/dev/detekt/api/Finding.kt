package dev.detekt.api

/**
 * Represents a detected problem in the source code.
 *
 * A finding has a source code position described as an entity and a message.
 * Entity references can also be considered for deeper characterization.
 */
class Finding(
    val entity: Entity,
    val message: String,
    val references: List<Entity> = emptyList(),
    val suppressReasons: List<String> = emptyList(),
) {
    init {
        require(message.isNotBlank()) { "The message should not be empty" }
    }

    override fun toString(): String =
        "Finding(entity=$entity, " +
            "message=$message, " +
            "references=$references, " +
            "suppressReasons=$suppressReasons)"
}
