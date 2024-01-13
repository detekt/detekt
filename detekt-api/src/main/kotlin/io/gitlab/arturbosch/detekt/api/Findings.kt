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

interface Finding2 : Compactable, HasEntity {
    val issue: Issue
    val references: List<Entity>
    val message: String
    val severity: Severity
        get() = Severity.DEFAULT
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

/**
 * Provides a compact string representation.
 */
interface Compactable {
    /**
     * Contract to format implementing object to a string representation.
     */
    fun compact(): String

    /**
     * Same as [compact] except the content should contain a substring which represents
     * this exact findings via a custom identifier.
     */
    fun compactWithSignature(): String = compact()
}
