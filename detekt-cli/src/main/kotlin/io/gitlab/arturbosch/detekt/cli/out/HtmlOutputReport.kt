package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.cli.ClasspathResourceConverter

private const val DEFAULT_TEMPLATE = "default-html-report-template.html"
private const val PLACEHOLDER_METRICS = "@@@metrics@@@"
private const val PLACEHOLDER_FINDINGS = "@@@findings@@@"

/**
 * Generates a HTML report containing rule violations and metrics.
 *
 * @author Marvin Ramin
 */
class HtmlOutputReport : OutputReport() {

    override val ending = "html"

    override val name = "HTML report"

    override fun render(detektion: Detektion) =
            ClasspathResourceConverter().convert(DEFAULT_TEMPLATE).openStream().bufferedReader().use { it.readText() }
                    .replace(PLACEHOLDER_METRICS, renderMetrics(detektion.metrics))
                    .replace(PLACEHOLDER_FINDINGS, renderFindings(detektion.findings))

    private fun renderMetrics(metrics: Collection<ProjectMetric>) = htmlSnippet {
        list(metrics) {
            text { "${it.type}: ${it.value}" }
        }
    }

    private fun renderFindings(findings: Map<String, List<Finding>>) = htmlSnippet {
        for ((group, groupFindings) in findings.filter { !it.value.isEmpty() }) {
            h3 { group }

            groupFindings.groupBy { it.id }.forEach { rule, findings ->
                if (!findings.isEmpty()) {
                    div("rule-container") {
                        span("rule") { rule }
                        span("description") { findings.first().issue.description }
                    }
                }

                list(findings) {
                    span("location") { "${it.file}:${it.location.source.line}:${it.location.source.column}" }

                    if (!it.message.isEmpty()) {
                        br()
                        span("message") { it.message }
                    }
                }
            }
        }
    }
}
