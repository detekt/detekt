package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import org.gradle.api.problems.ProblemGroup
import org.gradle.api.problems.ProblemId
import org.gradle.api.problems.ProblemReporter
import io.gitlab.arturbosch.detekt.api.Severity as DetektSeverity
import org.gradle.api.problems.Severity as GradleSeverity

class ProblemsApiConsoleReport(
    private val problemReporter: ProblemReporter? = null,
    override val id: String,
) : ConsoleReport() {

    override val priority: Int = -1

    override fun render(detektion: Detektion): String {
        val reporter = problemReporter ?: return ""
        val problemGroup = ProblemGroup.create("validation", "detekt")

        val allFindings = detektion.findings.values.flatten()

        allFindings.forEach { finding ->
            try {
                val problemId = ProblemId.create(
                    finding.id,
                    finding.messageOrDescription(),
                    problemGroup,
                )

                val loc = finding.entity.location
                val filePath = loc.filePath
                val line = loc.source.line
                val column = loc.source.column

                reporter.report(problemId) { spec ->
                    spec.lineInFileLocation(filePath.toString(), line, column)
                    spec.details(finding.messageOrDescription())
                    spec.severity(mapSeverity(finding.issue.severity))
                    spec.contextualLabel("Rule: ${finding.id}")
                }
            } catch (e: IllegalStateException) {
                System.err.println("Warning: Failed to report problem for ${finding.id}: ${e.message}")
            } catch (e: IllegalArgumentException) {
                System.err.println("Warning: Failed to report problem for ${finding.id}: ${e.message}")
            }
        }
        return ""
    }

    private fun mapSeverity(detektSeverity: DetektSeverity): GradleSeverity {
        return when (detektSeverity) {
            DetektSeverity.Warning -> GradleSeverity.WARNING
            DetektSeverity.CodeSmell -> GradleSeverity.WARNING
            DetektSeverity.Style -> GradleSeverity.WARNING
            DetektSeverity.Defect -> GradleSeverity.ERROR
            DetektSeverity.Minor -> GradleSeverity.WARNING
            DetektSeverity.Maintainability -> GradleSeverity.WARNING
            DetektSeverity.Security -> GradleSeverity.ERROR
            DetektSeverity.Performance -> GradleSeverity.WARNING
        }
    }
}
