package io.github.detekt.report.md

import io.github.detekt.metrics.ComplexityReportGenerator
import io.github.detekt.utils.MarkdownContent
import io.github.detekt.utils.codeBlock
import io.github.detekt.utils.emptyLine
import io.github.detekt.utils.h1
import io.github.detekt.utils.h2
import io.github.detekt.utils.h3
import io.github.detekt.utils.item
import io.github.detekt.utils.list
import io.github.detekt.utils.markdown
import io.github.detekt.utils.paragraph
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.internal.BuiltInOutputReport
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.math.max
import kotlin.math.min

private const val DETEKT_WEBSITE_BASE_URL = "https://detekt.dev"

private const val EXTRA_LINES_IN_SNIPPET = 3

/**
 * Contains rule violations in Markdown format report.
 * [See](https://detekt.dev/docs/introduction/configurations/#output-reports)
 */
class MdOutputReport : BuiltInOutputReport, OutputReport() {

    override val id: String = "MdOutputReport"
    override val ending: String = "md"

    override fun render(detektion: Detektion) = markdown {
        h1 { "detekt" }

        h2 { "Metrics" }
        renderMetrics(detektion.metrics)

        h2 { "Complexity Report" }
        renderComplexity(getComplexityMetrics(detektion))

        renderFindings(detektion.findings)
        emptyLine()

        paragraph {
            val detektLink = link("detekt version ${renderVersion()}", "$DETEKT_WEBSITE_BASE_URL/")
            "generated with $detektLink on ${renderDate()}"
        }
    }

    private fun renderVersion(): String = whichDetekt()

    private fun renderDate(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return "${OffsetDateTime.now(ZoneOffset.UTC).format(formatter)} UTC"
    }

    private fun getComplexityMetrics(detektion: Detektion): List<String> {
        return ComplexityReportGenerator.create(detektion).generate().orEmpty()
    }
}

private fun MarkdownContent.renderMetrics(metrics: Collection<ProjectMetric>) {
    list {
        metrics.forEach { item { "%,d ${it.type}".format(Locale.ROOT, it.value) } }
    }
}

private fun MarkdownContent.renderComplexity(complexityReport: List<String>) {
    list {
        complexityReport.forEach { item { it.trim() } }
    }
}

private fun MarkdownContent.renderGroup(group: RuleSet.Id, findings: List<Finding2>) {
    findings
        .groupBy { it.rule.id }
        .toList()
        .sortedBy { (rule, _) -> rule.value }
        .forEach { (rule, ruleFindings) ->
            renderRule(rule, group, ruleFindings)
        }
}

private fun MarkdownContent.renderRule(rule: Rule.Id, group: RuleSet.Id, findings: List<Finding2>) {
    h3 { "$group, $rule (%,d)".format(Locale.ROOT, findings.size) }
    paragraph { (findings.first().rule.description) }

    paragraph {
        link(
            "Documentation",
            "$DETEKT_WEBSITE_BASE_URL/docs/rules/${group.value.lowercase()}#${rule.value.lowercase()}"
        )
    }

    list {
        findings
            .sortedWith(compareBy({ it.file }, { it.location.source.line }, { it.location.source.column }))
            .forEach {
                item { renderFinding(it) }
            }
    }
}

private fun MarkdownContent.renderFindings(findings: Map<RuleSet.Id, List<Finding2>>) {
    val total = findings.values
        .asSequence()
        .map { it.size }
        .fold(0) { a, b -> a + b }

    h2 { "Findings (%,d)".format(Locale.ROOT, total) }

    findings
        .filter { it.value.isNotEmpty() }
        .toList()
        .sortedBy { (group, _) -> group.value }
        .forEach { (group, groupFindings) ->
            renderGroup(group, groupFindings)
        }
}

private fun MarkdownContent.renderFinding(finding: Finding2): String {
    val filePath = finding.location.filePath.relativePath ?: finding.location.filePath.absolutePath
    val location =
        "${filePath.invariantSeparatorsPathString}:${finding.location.source.line}:${finding.location.source.column}"

    val message = if (finding.message.isNotEmpty()) {
        codeBlock("") { finding.message }
    } else {
        ""
    }

    val psiFile = finding.entity.ktElement?.containingFile
    val snippet = if (psiFile != null) {
        val lineSequence = psiFile.text.splitToSequence('\n')
        snippetCode(lineSequence, finding.startPosition)
    } else {
        ""
    }

    return "$location\n$message\n$snippet"
}

private fun MarkdownContent.snippetCode(lines: Sequence<String>, location: SourceLocation): String {
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

    return codeBlock("kotlin") { text }
}

internal fun MarkdownContent.link(text: String, url: String) = "[$text]($url)"
