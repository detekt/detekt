package io.github.detekt.report.problems.api

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import org.gradle.api.Incubating
import org.gradle.api.problems.ProblemGroup
import org.gradle.api.problems.ProblemId
import org.gradle.api.problems.ProblemReporter
import org.gradle.api.problems.Problems
import org.gradle.api.problems.Severity
import javax.inject.Inject

@Incubating
class ProblemsApiOutputReport : OutputReport {

    private val problems: Problems?

    @Inject
    public constructor(problems: Problems) {
        this.problems = problems
    }

    public constructor() {
        this.problems = null
    }

    override val id: String = "problemsAPI"
    override val ending: String = "txt"

    override fun render(detektion: Detektion): String? {
        if (problems == null) {
            return "TEST-OK: Detekt found ${detektion.issues.size} issues."
        }

        val reporter: ProblemReporter = problems.reporter
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
            }
        }

        return null
    }
}

private fun mapSeverity(level: io.gitlab.arturbosch.detekt.api.Severity): Severity =
    when (level) {
        io.gitlab.arturbosch.detekt.api.Severity.Error -> Severity.ERROR
        io.gitlab.arturbosch.detekt.api.Severity.Warning -> Severity.WARNING
        io.gitlab.arturbosch.detekt.api.Severity.Info -> Severity.ADVICE
    }
