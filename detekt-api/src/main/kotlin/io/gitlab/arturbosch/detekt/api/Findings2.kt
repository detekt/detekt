package io.gitlab.arturbosch.detekt.api

import dev.drewhamilton.poko.Poko

interface Finding2 : HasEntity {
    val issue: Issue
    val references: List<Entity>
    val message: String
    val severity: Severity
    val autoCorrectEnabled: Boolean
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

fun Finding.toFinding2(): Finding2 {
    return when (this) {
        is CorrectableCodeSmell -> Finding2Impl(issue, entity, message, references, severity, autoCorrectEnabled)

        is CodeSmell -> Finding2Impl(issue, entity, message, references, severity)

        else -> error("wtf?")
    }
}

@Poko
private class Finding2Impl(
    override val issue: Issue,
    override val entity: Entity,
    override val message: String,
    override val references: List<Entity> = emptyList(),
    override val severity: Severity = Severity.DEFAULT,
    override val autoCorrectEnabled: Boolean = false,
) : Finding2 {
    init {
        require(message.isNotBlank()) { "The message should not be empty" }
    }
}
