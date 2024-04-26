package io.github.detekt.report.sarif

import io.github.detekt.sarif4k.ArtifactLocation
import io.github.detekt.sarif4k.Level
import io.github.detekt.sarif4k.Message
import io.github.detekt.sarif4k.PhysicalLocation
import io.github.detekt.sarif4k.Region
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Severity
import kotlin.io.path.Path
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.relativeTo

internal fun toResults(detektion: Detektion, basePath: String?): List<io.github.detekt.sarif4k.Result> =
    detektion.issues.map { it.toResult(basePath) }

internal fun Severity.toResultLevel() = when (this) {
    Severity.Error -> Level.Error
    Severity.Warning -> Level.Warning
    Severity.Info -> Level.Note
}

private fun Issue.toResult(basePath: String?): io.github.detekt.sarif4k.Result {
    return io.github.detekt.sarif4k.Result(
        ruleID = "detekt.${ruleInstance.ruleSetId}.${ruleInstance.name}",
        level = severity.toResultLevel(),
        locations = (listOf(location) + references.map { it.location }).map { it.toLocation(basePath) }.distinct(),
        message = Message(text = message)
    )
}

private fun Location.toLocation(basePath: String?): io.github.detekt.sarif4k.Location {
    return io.github.detekt.sarif4k.Location(
        physicalLocation = PhysicalLocation(
            region = Region(
                startLine = source.line.toLong(),
                startColumn = source.column.toLong(),
                endLine = endSource.line.toLong(),
                endColumn = endSource.column.toLong(),
            ),
            artifactLocation = if (basePath != null) {
                ArtifactLocation(
                    uri = path.relativeTo(Path(basePath)).invariantSeparatorsPathString,
                    uriBaseID = SRCROOT
                )
            } else {
                ArtifactLocation(uri = path.toUri().toString())
            }
        )
    )
}
