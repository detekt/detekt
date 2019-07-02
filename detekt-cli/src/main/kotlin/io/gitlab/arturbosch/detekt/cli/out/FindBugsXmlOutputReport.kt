package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.DetektVersion
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.Severity.Defect
import io.gitlab.arturbosch.detekt.api.Severity.Minor
import io.gitlab.arturbosch.detekt.api.Severity.Security
import java.lang.System.currentTimeMillis

/**
 * Generates an XML report following the structure of a FindBugs report.
 */
class FindBugsXmlOutputReport : OutputReport() {

    override val ending: String = "xml"

    override val name = "FindBugs XML report"

    override fun render(detektion: Detektion): String {
        val lines = mutableListOf<String>()
        lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        lines.add(buildString {
            append("<BugCollection")
            append(" tool=\"Detekt\"")
            append(" version=\"${DetektVersion.current.toXmlString()}\"")
            currentTimeMillis().let {
                append(" analysisTimestamp=\"$it\"")
                append(" timestamp=\"$it\"")
            }
            append(">")
        })

        detektion.findings.asSequence()
            .flatMap { entry -> entry.value.asSequence().map { entry.key to it } }
            .forEach { (category, finding) ->
                lines.add(buildString {
                    append("    <BugInstance")
                    append(" category=\"${category.toXmlString()}\"")
                    append(" type=\"${finding.issue.id.toXmlString()}\"")
                    append(" priority=\"${finding.issue.priority.toXmlString()}\"")
                    append(">")
                })

                lines.add("        <LongMessage>${finding.message.toXmlString()}</LongMessage>")

                lines.add(buildString {
                    append("        <SourceLine")
                    append(" sourcepath=\"${finding.location.file.toXmlString()}\"")
                    append(" sourcefile=\"${finding.location.file.toXmlString()}\"")
                    append(" start=\"${finding.location.source.line.toXmlString()}\"")
                    append(" startOffset=\"${finding.location.source.column.toXmlString()}\"")
                    append("/>")
                })

                lines.add("    </BugInstance>")
            }

        detektion.findings.asSequence()
            .flatMap { it.value.asSequence() }
            .filter { it.issue.id.isNotEmpty() && it.issue.description.isNotEmpty() }
            .map { TypeDescription(it.id, it.issue.description) }
            .groupBy(TypeDescription::type).asSequence()
            .map { TypeDescription(it.key, it.value.first().description) }
            .forEach { (type, description) ->
                lines.add("    <BugPattern type=\"${type.toXmlString()}\">")
                val htmlDescription = description
                    .replace("\r", "")
                    .replace("\n", "\n<br>")
                lines.add("        <Details>${htmlDescription.toXmlString()}</Details>")
                lines.add("    </BugPattern>")
            }

        lines.add("</BugCollection>")

        return lines.joinToString("\n") + "\n"
    }

    private data class TypeDescription(
        val type: String,
        val description: String
    )

    @Suppress("MagicNumber")
    private val Issue.priority
        get() = when (severity) {
            Security -> 1
            Defect -> 2
            Minor -> 4
            else -> 3
        }

    private fun Any.toXmlString() = XmlEscape.escapeXml(toString().trim())
}
