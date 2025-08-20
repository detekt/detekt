package dev.detekt.report.sarif

import dev.detekt.api.Issue
import dev.detekt.api.Severity
import dev.detekt.api.suppressed
import io.github.detekt.sarif4k.ArtifactLocation
import io.github.detekt.sarif4k.Level
import io.github.detekt.sarif4k.Message
import io.github.detekt.sarif4k.PhysicalLocation
import io.github.detekt.sarif4k.Region
import java.security.MessageDigest
import kotlin.io.path.invariantSeparatorsPathString

internal fun toResults(issues: List<Issue>): List<io.github.detekt.sarif4k.Result> =
    issues.filterNot { it.suppressed }.map { it.toResult() }

internal fun Severity.toResultLevel() = when (this) {
    Severity.Error -> Level.Error
    Severity.Warning -> Level.Warning
    Severity.Info -> Level.Note
}

private fun Issue.toResult(): io.github.detekt.sarif4k.Result =
    io.github.detekt.sarif4k.Result(
        ruleID = "detekt.${ruleInstance.ruleSetId}.${ruleInstance.id}",
        level = severity.toResultLevel(),
        locations = (listOf(location) + references.map { it.location }).map { it.toLocation() }.distinct(),
        message = Message(text = message),
        partialFingerprints = mapOf(
            "signature/v1" to entity.signature.sha1(),
        )
    )

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

private fun String.sha1(): String = MessageDigest
    .getInstance("SHA-1")
    .digest(toByteArray())
    .joinToString("") { it.toUByte().toString(radix = 16).padStart(2, '0') }
