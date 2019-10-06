package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.cli.ClasspathResourceConverter
import kotlinx.html.FlowContent
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.li
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.ul

private const val DEFAULT_TEMPLATE = "default-html-report-template.html"
private const val PLACEHOLDER_METRICS = "@@@metrics@@@"
private const val PLACEHOLDER_FINDINGS = "@@@findings@@@"

/**
 * Generates a HTML report containing rule violations and metrics.
 */
class HtmlOutputReport : OutputReport() {

    override val ending = "html"

    override val name = "HTML report"

    override fun render(detektion: Detektion) =
            ClasspathResourceConverter().convert(DEFAULT_TEMPLATE).openStream().bufferedReader().use { it.readText() }
                    .replace(PLACEHOLDER_METRICS, renderMetrics(detektion.metrics))
                    .replace(PLACEHOLDER_FINDINGS, renderFindings(detektion.findings))

    private fun renderMetrics(metrics: Collection<ProjectMetric>) = createHTML().div {
        ul {
            metrics.forEach {
                li { text("${it.type}: ${it.value}") }
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
        div("rule-container") {
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

    private fun FlowContent.renderFinding(it: Finding) {
        span("location") {
            text("${it.file}:${it.location.source.line}:${it.location.source.column}")
        }

        if (it.message.isNotEmpty()) {
            br()
            span("message") { text(it.message) }
        }
    }
}
