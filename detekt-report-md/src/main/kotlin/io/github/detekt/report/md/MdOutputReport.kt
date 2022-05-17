package io.github.detekt.report.md

import io.github.detekt.metrics.ComplexityReportGenerator
import io.github.detekt.psi.toUnifiedString
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

private const val DETEKT_WEBSITE_BASE_URL = "https://detekt.dev"

private const val EXTRA_LINES_IN_SNIPPET = 3

/**
 * Contains rule violations in Markdown format report.
 * [See:](https://detekt.dev/docs/introduction/configurations/#output-reports)
 */
class MdOutputReport : OutputReport() {
    override val ending: String = "md"

    override val name = "Markdown report"

    override fun render(detektion: Detektion) = markdown {
        h1("detekt")
        h2("Metrics") {
            renderMetrics(detektion.metrics)
        }
        h2("Complexity Report") {
            renderComplexity(getComplexityMetrics(detektion))
        }
        h2("Findings") {
            renderFindings(detektion.findings)
        }
        text(" ")
        val detektLink = link("detekt version ${renderVersion()}", "$DETEKT_WEBSITE_BASE_URL/")
        text("generated with $detektLink on ${renderDate()}")
    }.toString()

    private fun renderVersion(): String = whichDetekt() ?: "unknown"

    private fun renderDate(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return "${OffsetDateTime.now(ZoneOffset.UTC).format(formatter)} UTC"
    }

    private fun MdUtils.renderMetrics(metrics: Collection<ProjectMetric>) {
        metrics.forEach {
            listItem(("%,d ${it.type}".format(Locale.US, it.value)))
        }
    }

    private fun MdUtils.renderComplexity(complexityReport: List<String>) {
        complexityReport.forEach {
            listItem(it.trim())
        }
    }

    private fun getComplexityMetrics(detektion: Detektion): List<String> {
        return ComplexityReportGenerator.create(detektion).generate().orEmpty()
    }

    private fun MdUtils.renderFindings(findings: Map<String, List<Finding>>) {
        val total = findings.values
            .asSequence()
            .map { it.size }
            .fold(0) { a, b -> a + b }

        h3("Total: %,d".format(Locale.US, total))

        findings
            .filter { it.value.isNotEmpty() }
            .toList()
            .sortedBy { (group, _) -> group }
            .forEach { (group, groupFindings) ->
                orderedListItem(group) {
                    renderGroup(group, groupFindings)
                }
            }
    }

    private fun MdUtils.renderGroup(group: String, findings: List<Finding>) {
        findings
            .groupBy { it.id }
            .toList()
            .sortedBy { (rule, _) -> rule }
            .forEach { (rule, ruleFindings) ->
                renderRule(rule, group, ruleFindings)
            }
    }

    private fun MdUtils.renderRule(rule: String, group: String, findings: List<Finding>) {
        text("$rule: %,d ".format(Locale.US, findings.size))
        text(findings.first().issue.description)

        val ruleLink = link(
            "Documentation",
            "$DETEKT_WEBSITE_BASE_URL/docs/rules/${group.toLowerCase(Locale.US)}#${rule.toLowerCase(Locale.US)}"
        )
        text(ruleLink)

        findings
            .sortedWith(compareBy({ it.file }, { it.location.source.line }, { it.location.source.column }))
            .forEach {
                renderFinding(it)
            }
    }

    private fun MdUtils.renderFinding(finding: Finding) {
        val filePath = finding.location.filePath.relativePath ?: finding.location.filePath.absolutePath
        val location = "${filePath.toUnifiedString()}:${finding.location.source.line}:${finding.location.source.column}"

        listItem(location) {
            if (finding.message.isNotEmpty()) {
                codeBlock(finding.message, "")
            }

            val psiFile = finding.entity.ktElement?.containingFile
            if (psiFile != null) {
                val lineSequence = psiFile.text.splitToSequence('\n')
                snippetCode(lineSequence, finding.startPosition)
            }
        }
    }
}

internal fun MdUtils.snippetCode(lines: Sequence<String>, location: SourceLocation) {
    val dropLineCount = max(location.line - 1 - EXTRA_LINES_IN_SNIPPET, 0)
    val takeLineCount = EXTRA_LINES_IN_SNIPPET + 1 + min(location.line - 1, EXTRA_LINES_IN_SNIPPET)
    var currentLineNumber = dropLineCount + 1
    var text = ""

    val lineNoSpace = (currentLineNumber + takeLineCount).toString().length

    lines
        .drop(dropLineCount)
        .take(takeLineCount)
        .forEach { line ->
            val lineNo = ("$currentLineNumber ").take(lineNoSpace)
            text += "$lineNo $line\n"

            if (currentLineNumber == location.line) {
                val positions = currentLineNumber.toString().length
                val lineErr = "!".repeat(positions) + " ".repeat(location.column + lineNoSpace - positions)
                text += "$lineErr^ error\n"
            }
            currentLineNumber++
        }

    codeBlock(text, "kotlin")
}
