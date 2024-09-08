package io.github.detekt.report.html

import io.github.detekt.metrics.ComplexityReportGenerator
import io.github.detekt.utils.openSafeStream
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.RuleInstance
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.internal.BuiltInOutputReport
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.api.suppressed
import kotlinx.html.CommonAttributeGroupFacadeFlowInteractiveContent
import kotlinx.html.FlowContent
import kotlinx.html.FlowOrInteractiveContent
import kotlinx.html.HTMLTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer
import kotlinx.html.a
import kotlinx.html.attributesMapOf
import kotlinx.html.details
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.ul
import kotlinx.html.visit
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.io.path.invariantSeparatorsPathString

private const val DEFAULT_TEMPLATE = "default-html-report-template.html"
private const val PLACEHOLDER_METRICS = "@@@metrics@@@"
private const val PLACEHOLDER_ISSUES = "@@@issues@@@"
private const val PLACEHOLDER_COMPLEXITY_REPORT = "@@@complexity@@@"
private const val PLACEHOLDER_VERSION = "@@@version@@@"
private const val PLACEHOLDER_DATE = "@@@date@@@"

private const val DETEKT_WEBSITE_BASE_URL = "https://detekt.dev"

/**
 * Contains rule violations and metrics formatted in a human friendly way, so that it can be inspected in a web browser.
 * See: https://detekt.dev/configurations.html#output-reports
 */
class HtmlOutputReport : BuiltInOutputReport, OutputReport() {

    override val id: String = "HtmlOutputReport"
    override val ending = "html"

    private lateinit var basePath: Path
    override fun init(context: SetupContext) {
        super.init(context)
        basePath = context.basePath
    }

    override fun render(detektion: Detektion) =
        javaClass.getResource("/$DEFAULT_TEMPLATE")!!
            .openSafeStream()
            .bufferedReader()
            .use { it.readText() }
            .replace(PLACEHOLDER_VERSION, renderVersion())
            .replace(PLACEHOLDER_DATE, renderDate())
            .replace(PLACEHOLDER_METRICS, renderMetrics(detektion.metrics))
            .replace(PLACEHOLDER_COMPLEXITY_REPORT, renderComplexity(getComplexityMetrics(detektion)))
            .replace(PLACEHOLDER_ISSUES, renderIssues(detektion.issues.filterNot { it.suppressed }))

    private fun renderVersion(): String = whichDetekt()

    private fun renderDate(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return "${OffsetDateTime.now(ZoneOffset.UTC).format(formatter)} UTC"
    }

    private fun renderMetrics(metrics: Collection<ProjectMetric>) = createHTML().div {
        ul {
            metrics.forEach {
                li { text("%,d ${it.type}".format(Locale.ROOT, it.value)) }
            }
        }
    }

    private fun renderComplexity(complexityReport: List<String>) = createHTML().div {
        ul {
            complexityReport.forEach {
                li { text(it.trim()) }
            }
        }
    }

    private fun renderIssues(issues: List<Issue>) = createHTML().div {
        val total = issues.count()

        text("Total: %,d".format(Locale.ROOT, total))

        issues
            .groupBy { it.ruleInstance.ruleSetId }
            .toList()
            .sortedBy { (group, _) -> group.value }
            .forEach { (group, groupIssues) ->
                renderGroup(group, groupIssues)
            }
    }

    private fun FlowContent.renderGroup(group: RuleSet.Id, issues: List<Issue>) {
        h3 { text("$group: %,d".format(Locale.ROOT, issues.size)) }

        issues
            .groupBy { it.ruleInstance }
            .toList()
            .sortedBy { (ruleInstance, _) -> ruleInstance.id }
            .forEach { (ruleInstance, ruleIssues) ->
                renderRule(ruleInstance, ruleIssues)
            }
    }

    private fun FlowContent.renderRule(ruleInstance: RuleInstance, issues: List<Issue>) {
        val ruleId = ruleInstance.id
        val ruleName = ruleInstance.name.value
        val ruleSetId = ruleInstance.ruleSetId.value
        details {
            id = ruleId
            open = true

            summary("rule-container") {
                span("rule") { text("$ruleId: %,d ".format(Locale.ROOT, issues.size)) }
                span("description") { text(ruleInstance.description) }
            }

            a("$DETEKT_WEBSITE_BASE_URL/docs/rules/${ruleSetId.lowercase()}#${ruleName.lowercase()}") {
                +"Documentation"
            }

            ul {
                issues
                    .sortedWith(
                        compareBy(
                            { it.location.path },
                            { it.location.source.line },
                            { it.location.source.column },
                        )
                    )
                    .forEach {
                        li {
                            renderIssue(it)
                        }
                    }
            }
        }
    }

    private fun FlowContent.renderIssue(issue: Issue) {
        val pathString = issue.location.path.invariantSeparatorsPathString
        span("location") {
            text(
                "$pathString:${issue.location.source.line}:${issue.location.source.column}"
            )
        }

        if (issue.message.isNotEmpty()) {
            span("message") { text(issue.message) }
        }

        val absoluteFile = basePath.resolve(issue.location.path).toFile()
        if (absoluteFile.exists()) {
            absoluteFile.useLines {
                snippetCode(issue.ruleInstance.name, it, issue.location.source, issue.location.text.length())
            }
        }
    }

    private fun getComplexityMetrics(detektion: Detektion): List<String> =
        ComplexityReportGenerator.create(detektion).generate().orEmpty()
}

@HtmlTagMarker
private fun FlowOrInteractiveContent.summary(
    classes: String,
    block: SUMMARY.() -> Unit = {}
): Unit = SUMMARY(attributesMapOf("class", classes), consumer).visit(block)

private class SUMMARY(
    initialAttributes: Map<String, String>,
    override val consumer: TagConsumer<*>
) : HTMLTag("summary", consumer, initialAttributes, null, false, false),
    CommonAttributeGroupFacadeFlowInteractiveContent

private fun TextLocation.length(): Int = end - start
