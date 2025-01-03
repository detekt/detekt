package io.gitlab.arturbosch.detekt.api

/**
 * A code smell indicates any possible design problem inside a program's source code.
 * The type of a code smell is described by an [Issue].
 *
 * If the design problem manifests by different source locations, references to these
 * locations can be stored in additional [Entity]'s.
 */
class CodeSmell(
    override val entity: Entity,
    override val message: String,
    override val references: List<Entity> = emptyList(),
    override val suppressReasons: List<String> = emptyList(),
) : Finding {
    init {
        require(message.isNotBlank()) { "The message should not be empty" }
    }

    override fun toString(): String =
        "CodeSmell(entity=$entity, " +
            "message=$message, " +
            "references=$references, " +
            "suppressReasons=$suppressReasons)"
}
