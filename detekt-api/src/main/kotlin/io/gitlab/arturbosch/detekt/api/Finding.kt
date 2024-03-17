package io.gitlab.arturbosch.detekt.api

/**
 * Represents a problem detected by a rule on the source code
 *
 * A finding has an issue (information about the rule that detected the problem), a severity and a source code position
 * described as an entity. Entity references can also be considered for deeper characterization.
 */
interface Finding {
    val entity: Entity
    val references: List<Entity>
    val message: String
}

/**
 * Describes a source code position.
 */
interface HasEntity {
    val entity: Entity
    val location: Location
        get() = entity.location
    val startPosition: SourceLocation
        get() = location.source
    val charPosition: TextLocation
        get() = location.text
    val file: String
        get() = location.filePath.absolutePath.toString()
    val signature: String
        get() = entity.signature
}
