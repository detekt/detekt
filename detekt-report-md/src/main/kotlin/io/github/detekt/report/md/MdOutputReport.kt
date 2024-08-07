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
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.RuleInstance
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.getOrNull
import io.gitlab.arturbosch.detekt.api.internal.BuiltInOutputReport
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.api.name
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.io.path.absolute
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.relativeTo
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

    var basePath: Path? = null

    override fun init(context: SetupContext) {
        basePath = context.getOrNull<Path>(DETEKT_OUTPUT_REPORT_BASE_PATH_KEY)?.absolute()
    }

    override fun render(detektion: Detektion) = markdown {
        h1 { "detekt" }

        h2 { "Metrics" }
        renderMetrics(detektion.metrics)

        h2 { "Complexity Report" }
        renderComplexity(getComplexityMetrics(detektion))

        renderIssues(detektion.issues, basePath)
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

private fun MarkdownContent.renderGroup(issues: List<Issue>, basePath: Path?) {
    issues
        .groupBy { it.ruleInstance }
        .toList()
        .sortedBy { (ruleInstance, _) -> ruleInstance.id.value }
        .forEach { (ruleInstance, ruleIssues) ->
            renderRule(ruleInstance, ruleIssues, basePath)
        }
}

private fun MarkdownContent.renderRule(ruleInstance: RuleInstance, issues: List<Issue>, basePath: Path?) {
    val ruleId = ruleInstance.id
    val ruleName = ruleInstance.name.value
    val ruleSetId = ruleInstance.ruleSetId.value
    h3 { "$ruleSetId, $ruleId (%,d)".format(Locale.ROOT, issues.size) }
    paragraph { ruleInstance.description }

    paragraph {
        link(
            "Documentation",
            "$DETEKT_WEBSITE_BASE_URL/docs/rules/${ruleSetId.lowercase()}#${ruleName.lowercase()}"
        )
    }

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

private fun MarkdownContent.renderIssues(issues: List<Issue>, basePath: Path?) {
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

private fun MarkdownContent.renderIssue(issue: Issue, basePath: Path?): String {
    val filePath = basePath?.let { issue.location.path.relativeTo(it) } ?: issue.location.path
    val location =
        "${filePath.invariantSeparatorsPathString}:${issue.location.source.line}:${issue.location.source.column}"

    val message = if (issue.message.isNotEmpty()) {
        codeBlock("") { issue.message }
    } else {
        ""
    }

    val psiFile = issue.entity.ktElement.containingFile
    val lineSequence = psiFile.text.splitToSequence('\n')
    val snippet = snippetCode(lineSequence, issue.location.source)

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

@Suppress("UnusedReceiverParameter")
internal fun MarkdownContent.link(text: String, url: String) = "[$text]($url)"
