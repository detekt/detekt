package io.github.detekt.report.sarif

import io.github.detekt.sarif4k.ArtifactLocation
import io.github.detekt.sarif4k.Level
import io.github.detekt.sarif4k.Message
import io.github.detekt.sarif4k.PhysicalLocation
import io.github.detekt.sarif4k.Region
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.Severity
import kotlin.io.path.invariantSeparatorsPathString

internal fun toResults(detektion: Detektion): List<io.github.detekt.sarif4k.Result> =
    detektion.findings.flatMap { (ruleSetId, findings) ->
        findings.map { it.toResult(ruleSetId) }
    }

internal fun Severity.toResultLevel() = when (this) {
    Severity.Error -> Level.Error
    Severity.Warning -> Level.Warning
    Severity.Info -> Level.Note
}

private fun Finding2.toResult(ruleSetId: RuleSet.Id): io.github.detekt.sarif4k.Result {
    val code = entity.ktElement?.containingFile?.text

    return io.github.detekt.sarif4k.Result(
        ruleID = "detekt.$ruleSetId.${rule.id}",
        level = severity.toResultLevel(),
        locations = (listOf(location) + references.map { it.location }).map { it.toLocation(code) }.distinct().toList(),
        message = Message(text = message)
    )
}

private fun Location.toLocation(code: String?): io.github.detekt.sarif4k.Location {
    var endLine: Long? = null
    var endColumn: Long? = null

    if (code != null) {
        val snippet = code.take(text.end).split('\n')
        endLine = snippet.size.toLong()
        endColumn = snippet.last().length.toLong() + 1
    }

    return io.github.detekt.sarif4k.Location(
        physicalLocation = PhysicalLocation(
            region = Region(
                startLine = source.line.toLong(),
                startColumn = source.column.toLong(),
                endLine = endLine,
                endColumn = endColumn
            ),
            artifactLocation = if (filePath.relativePath != null) {
                ArtifactLocation(
                    uri = filePath.relativePath?.invariantSeparatorsPathString,
                    uriBaseID = SRCROOT
                )
            } else {
                ArtifactLocation(uri = filePath.absolutePath.toUri().toString())
            }
        )
    )
}
