package io.github.detekt.report.problems.api

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.RuleInstance
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Action
import org.gradle.api.problems.ProblemReporter
import org.gradle.api.problems.ProblemSpec
import org.gradle.api.problems.Problems
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.nio.file.Paths
import org.gradle.api.problems.Severity as GradleSeverity

class ProblemsApiConsoleReportSpec {

    private lateinit var problemsService: Problems
    private lateinit var problemReporter: ProblemReporter
    private lateinit var detektion: Detektion
    private lateinit var report: ProblemsApiConsoleReport

    @BeforeEach
    fun setUp() {
        problemsService = mock()
        problemReporter = mock()
        detektion = mock()
        whenever(problemsService.reporter).thenReturn(problemReporter)
        report = ProblemsApiConsoleReport(problemsService)
    }

    @Test
    fun `given a detekt issue, it correctly reports it to the Gradle Problems API`() {
        val ruleInstance = RuleInstance(
            id = "ClassNaming",
            ruleSetId = RuleSet.Id("style"),
            url = null,
            description = "",
            severity = io.gitlab.arturbosch.detekt.api.Severity.Error,
            active = true
        )

        val location = Issue.Location(
            source = SourceLocation(line = 4, column = 1),
            endSource = SourceLocation(line = 4, column = 15),
            text = TextLocation(start = 100, end = 114),
            path = Paths.get("src/main/kotlin/BadClass.kt")
        )

        val entity = Issue.Entity(
            signature = "TestEntitySignature",
            location = location,
        )

        val issue = Issue(
            ruleInstance = ruleInstance,
            entity = entity,
            message = "Class name should match the pattern...",
            severity = io.gitlab.arturbosch.detekt.api.Severity.Error,
            references = emptyList(),
            suppressReasons = emptyList()
        )
        whenever(detektion.issues).thenReturn(listOf(issue))

        val result = report.render(detektion)

        assertThat(result).isEqualTo("")

        val specCaptor = argumentCaptor<Action<ProblemSpec>>()
        verify(problemReporter).report(any(), specCaptor.capture())

        val spec: ProblemSpec = mock()
        specCaptor.firstValue.execute(spec)

        verify(spec).details("Class name should match the pattern...")
        verify(spec).severity(GradleSeverity.ERROR)
        verify(spec).lineInFileLocation(any(), eq(4))
        verify(spec).fileLocation(any())
    }

    @Test
    fun `given no issues, it does not report any problems`() {
        whenever(detektion.issues).thenReturn(emptyList())
        val result = report.render(detektion)

        assertThat(result).isEqualTo("")
        verify(problemReporter, never()).report(any(), any())
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "Error, ERROR",
            "Warning, WARNING",
            "Info, WARNING"
        ]
    )
    fun `mapSeverity maps Detekt severity to Gradle problems api severity`(
        detektSeverity: String,
        expectedGradleSeverity: String,
    ) {
        val detektSeverityEnum = io.gitlab.arturbosch.detekt.api.Severity.valueOf(detektSeverity)
        val gradleSeverity = mapSeverity(detektSeverityEnum)

        assertThat(gradleSeverity).isEqualTo(GradleSeverity.valueOf(expectedGradleSeverity))
    }
}
