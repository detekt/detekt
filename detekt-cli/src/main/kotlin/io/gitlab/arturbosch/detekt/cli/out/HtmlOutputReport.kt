package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.cli.ClasspathResourceConverter
import io.gitlab.arturbosch.detekt.cli.console.ComplexityReportGenerator
import kotlinx.html.CommonAttributeGroupFacadeFlowInteractiveContent
import kotlinx.html.FlowContent
import kotlinx.html.FlowOrInteractiveContent
import kotlinx.html.HTMLTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import kotlinx.html.br
import kotlinx.html.details
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.ul
import kotlinx.html.visit

private const val DEFAULT_TEMPLATE = "default-html-report-template.html"
private const val PLACEHOLDER_METRICS = "@@@metrics@@@"
private const val PLACEHOLDER_FINDINGS = "@@@findings@@@"
private const val PLACEHOLDER_COMPLEXITY_REPORT = "@@@complexity@@@"

/**
 * Generates a HTML report containing rule violations and metrics.
 */
class HtmlOutputReport : OutputReport() {

    override val ending = "html"

    override val name = "HTML report"

    override fun render(detektion: Detektion) =
            ClasspathResourceConverter().convert(DEFAULT_TEMPLATE).openStream().bufferedReader().use { it.readText() }
                    .replace(PLACEHOLDER_METRICS, renderMetrics(detektion.metrics))
                    .replace(PLACEHOLDER_COMPLEXITY_REPORT, renderComplexity(getComplexityMetrics(detektion)))
                    .replace(PLACEHOLDER_FINDINGS, renderFindings(detektion.findings))

    private fun renderMetrics(metrics: Collection<ProjectMetric>) = createHTML().div {
        ul {
            metrics.forEach {
                li { text("${it.type}: ${it.value}") }
            }
        }
    }

    private fun renderComplexity(complexityReport: List<String>) = createHTML().div {
        ul {
            complexityReport.forEach {
                li { text("$it") }
            }
        }
    }

    private fun renderFindings(findings: Map<String, List<Finding>>) = createHTML().div {
        findings
            .filter { it.value.isNotEmpty() }
            .forEach { (group, groupFindings) ->
                renderGroup(group, groupFindings)
            }
    }

    private fun FlowContent.renderGroup(group: String, findings: List<Finding>) {
        h3 { text(group) }

        findings
            .groupBy { it.id }
            .forEach { (rule, ruleFindings) ->
                renderRule(rule, ruleFindings)
            }
    }

    private fun FlowContent.renderRule(rule: String, findings: List<Finding>) {
        details {
            id = rule
            open = true

            summary("rule-container") {
                span("rule") { text("$rule ") }
                span("description") { text(findings.first().issue.description) }
            }

            ul {
                findings.forEach {
                    li {
                        renderFinding(it)
                    }
                }
            }
        }
    }

    private fun FlowContent.renderFinding(finding: Finding) {
        span("location") {
            text("${finding.file}:${finding.location.source.line}:${finding.location.source.column}")
        }

        if (finding.message.isNotEmpty()) {
            br()
            span("message") { text(finding.message) }
        }

        val psiFile = finding.entity.ktElement?.containingFile
        if (psiFile != null) {
            val lineSequence = psiFile.text.splitToSequence('\n')
            snippetCode(lineSequence, finding.startPosition, finding.charPosition.length())
        }
    }

    private fun getComplexityMetrics(detektion: Detektion): List<String> {
        var complexities = listOf<String>()
        val complexityReportGenerator = ComplexityReportGenerator.create(detektion)
        val complexityReport = complexityReportGenerator.generate()
        return if (complexityReport.isNullOrBlank()) complexities else {
            complexities = complexityReport.split("\n")
            return complexities.subList(1, complexities.size - 1)
        }
    }
}

@HtmlTagMarker
private fun FlowOrInteractiveContent.summary(
    classes: String? = null,
    block: SUMMARY.() -> Unit = {}
): Unit = SUMMARY(attributesMapOf("class", classes), consumer).visit(block)

private class SUMMARY(
    initialAttributes: Map<String, String>,
    override val consumer: TagConsumer<*>
) : HTMLTag(
    "summary",
    consumer,
    initialAttributes,
    null,
    false,
    false
),
    CommonAttributeGroupFacadeFlowInteractiveContent

private fun TextLocation.length(): Int = end - start
