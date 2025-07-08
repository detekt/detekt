package io.github.detekt.report.problems.api

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.SeverityLevel
import org.gradle.api.Incubating
import org.gradle.api.problems.ProblemGroup
import org.gradle.api.problems.ProblemId
import org.gradle.api.problems.ProblemReporter
import org.gradle.api.problems.Problems
import org.gradle.api.problems.Severity
import javax.inject.Inject

@Incubating
class ProblemsApiOutputReport @Inject constructor(
    private val problems: Problems,
) : OutputReport() {

    override val id: String = "problems"
    override val ending: String = ""

    override fun render(detektion: Detektion): String {
        val reporter: ProblemReporter = problems.reporter
        detektion.findings.forEach { entry ->
            entry.value.forEach { finding ->
                println("[Problems API] Reporting Detekt issue: ${finding.id}")
                val group = ProblemGroup.create("detekt findings", "detekt findings")
                val pid = ProblemId.create(finding.id, finding.message, group)

                reporter.report(pid) { spec ->
                    val file = finding.entity.location.filePath.toString()
                    spec.severity(mapSeverity(finding.severity))
                    spec.fileLocation(file)
                    spec.details(finding.message)
                }
            }
        }
        return ""
    }

    private fun mapSeverity(level: SeverityLevel): Severity =
        when (level) {
            SeverityLevel.ERROR -> Severity.ERROR
            SeverityLevel.WARNING -> Severity.WARNING
            SeverityLevel.INFO -> Severity.ADVICE
        }
}
