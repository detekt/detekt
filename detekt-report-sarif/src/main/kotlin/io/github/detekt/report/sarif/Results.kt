package io.github.detekt.report.sarif

import io.github.detekt.sarif4k.ArtifactLocation
import io.github.detekt.sarif4k.Level
import io.github.detekt.sarif4k.Message
import io.github.detekt.sarif4k.PhysicalLocation
import io.github.detekt.sarif4k.Region
import io.github.detekt.sarif4k.ReportingDescriptor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.suppressed
import kotlin.io.path.invariantSeparatorsPathString

internal fun toResults(detektion: Detektion, rules: List<ReportingDescriptor>): List<io.github.detekt.sarif4k.Result> =
    detektion.issues.filterNot { it.suppressed }.map { it.toResult(rules) }

internal fun Severity.toResultLevel() = when (this) {
    Severity.Error -> Level.Error
    Severity.Warning -> Level.Warning
    Severity.Info -> Level.Note
}

private fun Issue.toResult(rules: List<ReportingDescriptor>): io.github.detekt.sarif4k.Result {
    val ruleId = "detekt.${ruleInstance.ruleSetId}.${ruleInstance.id}"
    val suppressResultLevel = rules.firstOrNull { it.id == ruleId }
        ?.defaultConfiguration
        ?.level == severity.toResultLevel()

    return io.github.detekt.sarif4k.Result(
        ruleID = ruleId,
        level = if (suppressResultLevel) null else severity.toResultLevel(),
        locations = (listOf(location) + references.map { it.location }).map { it.toLocation() }.distinct(),
        message = Message(text = message)
    )
}

private fun Issue.Location.toLocation(): io.github.detekt.sarif4k.Location =
    io.github.detekt.sarif4k.Location(
        physicalLocation = PhysicalLocation(
            region = Region(
                startLine = source.line.toLong(),
                startColumn = source.column.toLong(),
                endLine = endSource.line.toLong(),
                endColumn = endSource.column.toLong(),
            ),
            artifactLocation = ArtifactLocation(
                uri = path.invariantSeparatorsPathString,
                uriBaseID = SRCROOT
            )
        )
    )
