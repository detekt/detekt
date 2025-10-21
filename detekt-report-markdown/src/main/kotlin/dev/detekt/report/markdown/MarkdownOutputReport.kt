package dev.detekt.report.markdown

import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.OutputReport
import dev.detekt.api.ProjectMetric
import dev.detekt.api.RuleInstance
import dev.detekt.api.SetupContext
import dev.detekt.api.SourceLocation
import dev.detekt.api.internal.whichDetekt
import dev.detekt.api.suppressed
import dev.detekt.metrics.ComplexityReportGenerator
import dev.detekt.utils.MarkdownContent
import dev.detekt.utils.codeBlock
import dev.detekt.utils.emptyLine
import dev.detekt.utils.h1
import dev.detekt.utils.h2
import dev.detekt.utils.h3
import dev.detekt.utils.item
import dev.detekt.utils.list
import dev.detekt.utils.markdown
import dev.detekt.utils.paragraph
import java.nio.file.Path
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
 */
class MarkdownOutputReport : OutputReport {
    override val id: String = "markdown"

    private lateinit var basePath: Path
    override fun init(context: SetupContext) {
        super.init(context)
        basePath = context.basePath
    }

    override fun render(detektion: Detektion) = markdown {
        h1 { "detekt" }

        h2 { "Metrics" }
        renderMetrics(detektion.metrics)

        h2 { "Complexity Report" }
        renderComplexity(getComplexityMetrics(detektion))

        renderIssues(detektion.issues.filterNot { it.suppressed }, basePath)
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

    private fun getComplexityMetrics(detektion: Detektion): List<String> =
        ComplexityReportGenerator.create(detektion).generate().orEmpty()
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

private fun MarkdownContent.renderGroup(issues: List<Issue>, basePath: Path) {
    issues
        .groupBy { it.ruleInstance }
        .toList()
        .sortedBy { (ruleInstance, _) -> ruleInstance.id }
        .forEach { (ruleInstance, ruleIssues) ->
            renderRule(ruleInstance, ruleIssues, basePath)
        }
}

private fun MarkdownContent.renderRule(ruleInstance: RuleInstance, issues: List<Issue>, basePath: Path) {
    val ruleId = ruleInstance.id
    val ruleSetId = ruleInstance.ruleSetId.value
    h3 { "$ruleSetId, $ruleId (%,d)".format(Locale.ROOT, issues.size) }
    paragraph { ruleInstance.description }

    ruleInstance.url?.let { paragraph { link("Documentation", it.toString()) } }

    list {
        issues
            .sortedWith(
                compareBy(
                    { it.location.path },
                    { it.location.source.line },
                    { it.location.source.column },
                )
            )
            .forEach {
                item { renderIssue(it, basePath) }
            }
    }
}

private fun MarkdownContent.renderIssues(issues: List<Issue>, basePath: Path) {
    val total = issues.count()

    h2 { "Issues (%,d)".format(Locale.ROOT, total) }

    issues
        .groupBy { it.ruleInstance.ruleSetId }
        .toList()
        .sortedBy { (group, _) -> group.value }
        .forEach { (_, groupIssues) ->
            renderGroup(groupIssues, basePath)
        }
}

private fun MarkdownContent.renderIssue(issue: Issue, basePath: Path): String {
    val filePath = issue.location.path
    val location =
        "${filePath.invariantSeparatorsPathString}:${issue.location.source.line}:${issue.location.source.column}"

    val message = if (issue.message.isNotEmpty()) {
        codeBlock("") { issue.message }
    } else {
        ""
    }

    val absoluteFile = basePath.resolve(issue.location.path).toFile()
    val snippet = if (absoluteFile.exists()) {
        absoluteFile.useLines { snippetCode(it, issue.location.source) }
    } else {
        null
    }

    return listOfNotNull(location, message, snippet).joinToString("\n")
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

@Suppress("UnusedReceiverParameter")
internal fun MarkdownContent.link(text: String, url: String) = "[$text]($url)"
