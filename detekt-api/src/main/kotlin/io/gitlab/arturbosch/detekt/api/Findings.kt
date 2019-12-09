package io.gitlab.arturbosch.detekt.api

/**
 * Base interface of detection findings. Inherits a bunch of useful behaviour
 * from sub interfaces.
 *
 * Basic behaviour of a finding is that is can be assigned to an id and a source code position described as
 * an entity. Metrics and entity references can also considered for deeper characterization.
 */
interface Finding : Compactable, HasEntity, HasMetrics {
    val id: String
    val issue: Issue
    val references: List<Entity>
    val message: String

    /**
     * Explanation why this finding was raised.
     */
    fun messageOrDescription(): String
}

/**
 * Describes a source code position.
 */
@Suppress("DEPRECATION")
interface HasEntity {
    val entity: Entity
    val location: Location
        get() = entity.location
    @Deprecated("Will be removed in the future. Use queries on 'ktElement' instead.")
    val locationAsString: String
        get() = location.locationString
    val startPosition: SourceLocation
        get() = location.source
    val charPosition: TextLocation
        get() = location.text
    val file: String
        get() = location.file
    val signature: String
        get() = entity.signature
    @Deprecated("Will be removed in the future. Use queries on 'ktElement' instead.")
    val name: String
        get() = entity.name
    @Deprecated("Will be removed in the future. Use queries on 'ktElement' instead.")
    val inClass: String
        get() = entity.className
}

/**
 * Adds metric container behaviour.
 */
interface HasMetrics {
    val metrics: List<Metric>
    /**
     * Finds the first metric matching given [type].
     */
    fun metricByType(type: String): Metric? = metrics.find { it.type == type }
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
