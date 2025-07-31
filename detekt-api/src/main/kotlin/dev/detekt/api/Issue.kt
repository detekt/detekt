package dev.detekt.api

import dev.drewhamilton.poko.Poko
import java.net.URI
import java.nio.file.Path

/**
 * Represents a problem detected by detekt on the source code
 *
 * An Issue has information about the rule that detected the problem, a severity and an entity with information
 * about the position. Entity references can also be considered for deeper characterization.
 */
@Poko
class Issue(
    val ruleInstance: RuleInstance,
    val entity: Entity,
    val references: List<Entity>,
    val message: String,
    val severity: Severity,
    val suppressReasons: List<String>,
) {

    val location: Location
        get() = entity.location

    @Poko
    class Entity(
        val signature: String,
        val location: Location,
    )

    @Poko
    class Location(
        val source: SourceLocation,
        val endSource: SourceLocation,
        val text: TextLocation,
        val path: Path,
    ) : Comparable<Location> {
        override fun compareTo(other: Location): Int = compareValuesBy(this, other, { it.path }, { it.source })
    }
}

val Issue.suppressed: Boolean
    get() = suppressReasons.isNotEmpty()

@Poko
class RuleInstance(
    val id: String,
    val ruleSetId: RuleSet.Id,
    val url: URI?,
    val description: String,
    val severity: Severity,
    val active: Boolean,
)
