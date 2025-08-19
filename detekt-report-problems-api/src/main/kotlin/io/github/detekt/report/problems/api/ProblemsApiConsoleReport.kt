package io.github.detekt.report.problems.api

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Severity
import org.gradle.api.Incubating
import org.gradle.api.logging.Logging
import org.gradle.api.problems.ProblemGroup
import org.gradle.api.problems.ProblemId
import org.gradle.api.problems.ProblemReporter
import org.gradle.api.problems.Problems
import javax.inject.Inject
import org.gradle.api.problems.Severity as GradleSeverity

@Incubating
class ProblemsApiConsoleReport @Inject constructor(
    private val problems: Problems,
) : ConsoleReport {

    private val logger = Logging.getLogger("detekt-problems-api-reporter")

    override val id: String = "problemsAPI"

    override fun render(detektion: Detektion): String? {
        val reporter: ProblemReporter? = problems.reporter
        val reportLines = mutableListOf<String>()

        if (reporter == null) {
            logger.debug("Problems API reporter not available")
            return null
        }

        detektion.issues.forEach { issue ->
            val group = ProblemGroup.create("validation", "detekt issue")
            val id = ProblemId.create(issue.ruleInstance.id, issue.message, group)
            reporter.report(id) { spec ->
                val filePath = issue.location.path.toString()
                val line = issue.location.source.line
                spec.fileLocation(filePath)
                spec.lineInFileLocation(filePath, line)
                spec.details(issue.message)
                spec.severity(mapSeverity(issue.severity))
                reportLines.add(
                    "${issue.location.path}:${issue.location.source.line} " +
                        "[${issue.ruleInstance.id}] ${issue.message}"
                )
            }
        }
        return reportLines.joinToString(separator = System.lineSeparator())
    }
}

fun mapSeverity(level: Severity): GradleSeverity =
    when (level) {
        Severity.Error -> GradleSeverity.ERROR
        Severity.Warning -> GradleSeverity.WARNING
        Severity.Info -> GradleSeverity.WARNING // Changed from ADVICE to WARNING for better visibility
    }
