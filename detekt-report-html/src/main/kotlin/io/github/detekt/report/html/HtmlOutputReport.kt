package io.github.detekt.report.html

import io.github.detekt.metrics.ComplexityReportGenerator
import io.github.detekt.utils.openSafeStream
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.internal.BuiltInOutputReport
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
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
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.io.path.invariantSeparatorsPathString

private const val DEFAULT_TEMPLATE = "default-html-report-template.html"
private const val PLACEHOLDER_METRICS = "@@@metrics@@@"
private const val PLACEHOLDER_FINDINGS = "@@@findings@@@"
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

    override fun render(detektion: Detektion) =
        javaClass.getResource("/$DEFAULT_TEMPLATE")!!
            .openSafeStream()
            .bufferedReader()
            .use { it.readText() }
            .replace(PLACEHOLDER_VERSION, renderVersion())
            .replace(PLACEHOLDER_DATE, renderDate())
            .replace(PLACEHOLDER_METRICS, renderMetrics(detektion.metrics))
            .replace(PLACEHOLDER_COMPLEXITY_REPORT, renderComplexity(getComplexityMetrics(detektion)))
            .replace(PLACEHOLDER_FINDINGS, renderFindings(detektion.findings))

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

    private fun renderFindings(findings: Map<RuleSet.Id, List<Finding2>>) = createHTML().div {
        val total = findings.values
            .asSequence()
            .map { it.size }
            .fold(0) { a, b -> a + b }

        text("Total: %,d".format(Locale.ROOT, total))

        findings
            .filter { it.value.isNotEmpty() }
            .toList()
            .sortedBy { (group, _) -> group.value }
            .forEach { (group, groupFindings) ->
                renderGroup(group, groupFindings)
            }
    }

    private fun FlowContent.renderGroup(group: RuleSet.Id, findings: List<Finding2>) {
        h3 { text("$group: %,d".format(Locale.ROOT, findings.size)) }

        findings
            .groupBy { it.rule.id }
            .toList()
            .sortedBy { (rule, _) -> rule.value }
            .forEach { (rule, ruleFindings) ->
                renderRule(rule, group, ruleFindings)
            }
    }

    private fun FlowContent.renderRule(rule: Rule.Id, group: RuleSet.Id, findings: List<Finding2>) {
        details {
            id = rule.value
            open = true

            summary("rule-container") {
                span("rule") { text("$rule: %,d ".format(Locale.ROOT, findings.size)) }
                span("description") { text(findings.first().rule.description) }
            }

            a("$DETEKT_WEBSITE_BASE_URL/docs/rules/${group.value.lowercase()}#${rule.value.lowercase()}") {
                +"Documentation"
            }

            ul {
                findings
                    .sortedWith(compareBy({ it.file }, { it.location.source.line }, { it.location.source.column }))
                    .forEach {
                        li {
                            renderFinding(it)
                        }
                    }
            }
        }
    }

    private fun FlowContent.renderFinding(finding: Finding2) {
        val filePath = finding.location.filePath.relativePath ?: finding.location.filePath.absolutePath
        val pathString = filePath.invariantSeparatorsPathString
        span("location") {
            text(
                "$pathString:${finding.location.source.line}:${finding.location.source.column}"
            )
        }

        if (finding.message.isNotEmpty()) {
            span("message") { text(finding.message) }
        }

        val psiFile = finding.entity.ktElement?.containingFile
        if (psiFile != null) {
            val lineSequence = psiFile.text.splitToSequence('\n')
            snippetCode(finding.rule.id, lineSequence, finding.startPosition, finding.charPosition.length())
        }
    }

    private fun getComplexityMetrics(detektion: Detektion): List<String> {
        return ComplexityReportGenerator.create(detektion).generate().orEmpty()
    }
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
