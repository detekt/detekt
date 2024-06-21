package io.gitlab.arturbosch.detekt.api

/**
 * A code smell indicates any possible design problem inside a program's source code.
 * The type of a code smell is described by an [Issue].
 *
 * If the design problem manifests by different source locations, references to these
 * locations can be stored in additional [Entity]'s.
 */
open class CodeSmell(
    final override val entity: Entity,
    final override val message: String,
    final override val references: List<Entity> = emptyList()
) : Finding {
    init {
        require(message.isNotBlank()) { "The message should not be empty" }
    }

    override fun toString(): String =
        "CodeSmell(entity=$entity, " +
            "message=$message, " +
            "references=$references)"
}

/**
 * Represents a code smell that can be auto-corrected.
 *
 * @see CodeSmell
 */
open class CorrectableCodeSmell(
    entity: Entity,
    message: String,
    references: List<Entity> = emptyList(),
    val autoCorrectEnabled: Boolean
) : CodeSmell(
    entity,
    message,
    references
) {
    override fun toString(): String =
        "CorrectableCodeSmell(" +
            "autoCorrectEnabled=$autoCorrectEnabled, " +
            "entity=$entity, " +
            "message=$message, " +
            "references=$references)"
}
