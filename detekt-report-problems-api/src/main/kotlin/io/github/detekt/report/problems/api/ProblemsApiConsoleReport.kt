package io.github.detekt.report.problems.api

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import org.gradle.api.Incubating
import org.gradle.api.problems.ProblemGroup
import org.gradle.api.problems.ProblemId
import org.gradle.api.problems.ProblemReporter
import org.gradle.api.problems.Problems
import org.gradle.api.problems.Severity
import javax.inject.Inject

@Incubating
class ProblemsApiConsoleReport : ConsoleReport {

    private val problems: Problems?

    override val id: String = "problemsAPI"

    @Inject
    public constructor(problems: Problems) {
        this.problems = problems
    }

    public constructor() {
        this.problems = null
    }

    override fun render(detektion: Detektion): String? {
        val reporter: ProblemReporter? = problems?.reporter ?: null
        val reportLines = mutableListOf<String>()

        detektion.issues.forEach { issue ->
            val group = ProblemGroup.create("validation", "detekt issue")
            val id = ProblemId.create(issue.ruleInstance.id, issue.message, group)
            reporter?.report(id) { spec ->
                val filePath = issue.location.path.toString()
                val line = issue.location.source.line
                spec.fileLocation(filePath)
                spec.lineInFileLocation(filePath, line)
                spec.details(issue.message)
                spec.severity(mapSeverity(issue.severity))
                reportLines.add(
                    "${issue.location.path}:${issue.location.source.line} [${issue.ruleInstance.id}] ${issue.message}"
                )
            }
        }
        return reportLines.joinToString(separator = System.lineSeparator())
    }
}

fun mapSeverity(level: io.gitlab.arturbosch.detekt.api.Severity): Severity =
    when (level) {
        io.gitlab.arturbosch.detekt.api.Severity.Error -> Severity.ERROR
        io.gitlab.arturbosch.detekt.api.Severity.Warning -> Severity.WARNING
        io.gitlab.arturbosch.detekt.api.Severity.Info -> Severity.ADVICE
    }
