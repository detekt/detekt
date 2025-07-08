package io.github.detekt.report.problems.api

import io.github.detekt.psi.FilePath
import io.github.detekt.report.problems.api.ProblemsApiOutputReport
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SeverityLevel
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import org.gradle.api.problems.ProblemReporter
import org.gradle.api.problems.Problems
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.nio.file.Paths

class ProblemsApiOutputReportSpec {

    @Mock
    private lateinit var mockProblemsService: Problems

    @Mock
    private lateinit var mockProblemReporter: ProblemReporter

    private lateinit var report: ProblemsApiOutputReport

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(mockProblemsService.reporter).thenReturn(mockProblemReporter)
        report = ProblemsApiOutputReport(mockProblemsService)
    }

    @Test
    fun `render handles multiple findings`() {
        val finding1 = createTestFinding("Rule1", "Message1", SeverityLevel.WARNING, "src/File1.kt", 1, 1)
        val finding2 = createTestFinding("Rule2", "Message2", SeverityLevel.INFO, "src/File2.kt", 2, 2)

        val detektion = TestDetektion(finding1, finding2)
        report.render(detektion)

        verify(mockProblemReporter, times(2)).report(any(), any())
    }

    @Test
    fun `render does nothing with empty findings`() {
        val detektion = TestDetektion()
        report.render(detektion)
        verify(mockProblemReporter, times(0)).report(any(), any())
    }

    private fun createTestFinding(
        ruleName: String,
        message: String,
        severityLevel: SeverityLevel,
        filePath: String,
        line: Int,
        column: Int,
    ): Finding {
        val issue = Issue(ruleName, mapSeverity(severityLevel), message, Debt.Companion.FIVE_MINS)
        val location = Location(
            source = SourceLocation(line, column),
            text = TextLocation(0, 0),
            filePath = FilePath(Paths.get(filePath))
        )
        return CodeSmell(issue, Entity(ruleName, "TestEntity", location), message)
    }

    private fun mapSeverity(level: SeverityLevel): Severity =
        when (level) {
            SeverityLevel.ERROR -> Severity.CodeSmell
            SeverityLevel.WARNING -> Severity.Maintainability
            SeverityLevel.INFO -> Severity.Style
        }

    class TestDetektion(vararg findingsToAdd: Finding) : Detektion {
        override val findings: Map<String, List<Finding>> =
            findingsToAdd.groupBy { it.issue.id }

        override val notifications: Collection<Notification> = emptyList()
        override val metrics: Collection<ProjectMetric> = emptyList()
        override fun add(notification: Notification) {
            // no-op
        }
        override fun add(projectMetric: ProjectMetric) {
            // no-op
        }
        override fun <V> addData(key: Key<V>, value: V) {
            // no-op
        }
        override fun <V> getData(key: Key<V>): V? = null
    }
}
