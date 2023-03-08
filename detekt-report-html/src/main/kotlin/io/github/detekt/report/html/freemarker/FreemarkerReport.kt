package io.github.detekt.report.html.freemarker

import io.github.detekt.metrics.ComplexityReportGenerator
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

private const val DETEKT_WEBSITE_BASE_URL = "https://detekt.dev"

data class FreemarkerReport(
    val detektion: Detektion,
    val metadata: Metadata
) {

    data class Detektion(
        val metrics: List<ProjectMetric>,
        val complexity: List<String>,
        val findings: Findings
    ) {

        data class Findings(
            val total: Int,
            val groups: List<Group>
        ) {

            data class Group(
                val name: String,
                val rules: List<Rule>
            ) {

                data class Rule(
                    val name: String,
                    val description: String,
                    val documentationUrl: String,
                    val findings: List<Finding>
                ) {

                    data class Finding(
                        val path: Path,
                        val line: Int,
                        val column: Int,
                        val message: String
                    )
                }
            }
        }
    }

    data class Metadata(
        val detektVersion: String,
        val currentDate: String
    )
}

fun createFreemarkerReport(detektion: Detektion): FreemarkerReport {
    return FreemarkerReport(
        metadata = FreemarkerReport.Metadata(
            detektVersion = whichDetekt(),
            currentDate = renderDate()
        ),
        detektion = FreemarkerReport.Detektion(
            metrics = detektion.metrics.toList(),
            complexity =  ComplexityReportGenerator.create(detektion).generate().orEmpty(),
            findings = FreemarkerReport.Detektion.Findings(
                total = detektion.findings.values.map { it.size }.sum(),
                groups = detektion.findings
                    .filter { it.value.isNotEmpty() }
                    .toList()
                    .sortedBy { (group, _) -> group }
                    .map { (group, groupFindings) ->
                        FreemarkerReport.Detektion.Findings.Group(
                            name = group,
                            rules = groupFindings.groupBy { it.id }
                                .toList()
                                .sortedBy { (rule, _) -> rule }
                                .map { (rule, ruleFindings) ->
                                    FreemarkerReport.Detektion.Findings.Group.Rule(
                                        name = rule,
                                        description = ruleFindings.first().issue.description,
                                        documentationUrl = "${DETEKT_WEBSITE_BASE_URL}/docs/rules/${group.lowercase(Locale.US)}#${rule.lowercase(Locale.US)}",
                                        findings = ruleFindings
                                            .sortedWith(compareBy({ it.file }, { it.location.source.line }, { it.location.source.column }))
                                            .map { finding ->
                                                FreemarkerReport.Detektion.Findings.Group.Rule.Finding(
                                                    path = (finding.location.filePath.relativePath ?: finding.location.filePath.absolutePath),
                                                    line = finding.location.source.line,
                                                    column = finding.location.source.column,
                                                    message = finding.message
                                                )
                                            }
                                    )
                                }
                        )
                    }
            )
        )
    )
}

private fun renderDate(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return "${OffsetDateTime.now(ZoneOffset.UTC).format(formatter)} UTC"
}

