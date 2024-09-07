package io.github.detekt.report.html

import io.github.detekt.metrics.CognitiveComplexity
import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.TestSetupContext
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInstance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.writeText

class HtmlOutputReportSpec {
    private val basePath = Path("src/test/resources").absolute()
    private val htmlReport = HtmlOutputReport().apply { init(TestSetupContext(basePath = basePath)) }

    @Test
    fun `renders the HTML headers correctly`() {
        val result = htmlReport.render(TestDetektion())

        assertThat(result).startsWith("<!DOCTYPE html>\n<html lang=\"en\">")
        assertThat(result).endsWith("</html>\n")

        assertThat(result).contains("<h2>Metrics</h2>")
        assertThat(result).contains("<h2>Complexity Report</h2>")
        assertThat(result).contains("<h2>Issues</h2>")
    }

    @Test
    fun `renders the 'generated with' text correctly`() {
        val version = whichDetekt()
        val header =
            """generated with <a href="https://detekt.dev/">detekt version $version</a> on """

        val result = htmlReport.render(TestDetektion())

        assertThat(result).contains(header)
        assertThat(result).doesNotContain("@@@date@@@")
    }

    @Test
    fun `contains the total number of issues`() {
        val result = htmlReport.render(createTestDetektionWithMultipleSmells())

        assertThat(result).contains("Total: 3")
    }

    @Test
    fun `contains no issues`() {
        val detektion = TestDetektion()
        val result = htmlReport.render(detektion)
        assertThat(result).contains("Total: 0")
    }

    @Test
    fun `renders the right file locations`() {
        val result = htmlReport.render(createTestDetektionWithMultipleSmells())

        assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample1.kt:11:1</span>")
        assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample2.kt:22:2</span>")
        assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample3.kt:33:3</span>")
    }

    @Test
    fun `renders the right number of issues per rule`() {
        val result = htmlReport.render(createTestDetektionWithMultipleSmells())

        assertThat(result).contains("<span class=\"rule\">rule_a/id: 2 </span>")
        assertThat(result).contains("<span class=\"rule\">rule_b: 1 </span>")
    }

    @Test
    fun `renders the right violation messages for the rules`() {
        val result = htmlReport.render(createTestDetektionWithMultipleSmells())

        assertThat(result).contains("<span class=\"message\">Issue message 1</span>")
        assertThat(result).contains("<span class=\"message\">Issue message 2</span>")
        assertThat(result).doesNotContain("<span class=\"message\"></span>")
    }

    @Test
    fun `renders the right violation description for the rules`() {
        val result = htmlReport.render(createTestDetektionWithMultipleSmells())

        assertThat(result).contains("<span class=\"description\">Description rule_a</span>")
        assertThat(result).contains("<span class=\"description\">Description rule_b</span>")
    }

    @Test
    fun `renders the right documentation links for the rules`() {
        val detektion = TestDetektion(
            createIssue(createRuleInstance("ValCouldBeVar", "Style")),
            createIssue(createRuleInstance("EmptyBody", "empty")),
            createIssue(createRuleInstance("EmptyIf", "empty")),
        )

        val result = htmlReport.render(detektion)
        assertThat(result).contains("<a href=\"https://detekt.dev/docs/rules/style#valcouldbevar\">Documentation</a>")
        assertThat(result).contains("<a href=\"https://detekt.dev/docs/rules/empty#emptybody\">Documentation</a>")
        assertThat(result).contains("<a href=\"https://detekt.dev/docs/rules/empty#emptyif\">Documentation</a>")
    }

    @Test
    fun `renders a metric report correctly`() {
        val detektion = TestDetektion(
            metrics = listOf(ProjectMetric("M1", 10_000), ProjectMetric("M2", 2))
        )
        val result = htmlReport.render(detektion)
        assertThat(result).contains("<li>10,000 M1</li>")
        assertThat(result).contains("<li>2 M2</li>")
    }

    @Test
    fun `renders the complexity report correctly`() {
        val detektion = TestDetektion()
        detektion.putUserData(complexityKey, 10)
        detektion.putUserData(CognitiveComplexity.KEY, 10)
        detektion.putUserData(sourceLinesKey, 20)
        detektion.putUserData(logicalLinesKey, 10)
        detektion.putUserData(commentLinesKey, 2)
        detektion.putUserData(linesKey, 2222)
        val result = htmlReport.render(detektion)
        assertThat(result).contains("<li>2,222 lines of code (loc)</li>")
        assertThat(result).contains("<li>20 source lines of code (sloc)</li>")
        assertThat(result).contains("<li>10 logical lines of code (lloc)</li>")
    }

    @Test
    fun `renders a blank complexity report correctly`() {
        val result = htmlReport.render(createTestDetektionWithMultipleSmells())
        assertThat(result).contains("<h2>Complexity Report</h2>\n\n<div>\n  <ul></ul>\n</div>")
    }

    @Test
    fun `asserts that the generated HTML is the same as expected`() {
        val expectedString = readResourceContent("HtmlOutputFormatTest.html")
        val expected = createTempFileForTest("expected-report", ".html").apply { writeText(expectedString) }

        val result = htmlReport.render(createTestDetektionWithMultipleSmells())
            .replace(generatedRegex, REPLACEMENT)
        val actual = createTempFileForTest("actual-report", ".html").apply { writeText(result) }

        assertThat(actual).hasSameTextualContentAs(expected)
    }

    @Test
    fun `asserts that the generated HTML is the same even if we change the order of the issues`() {
        val issues = issues()
        val reversedIssues = issues.reversedArray()

        val firstReport = createReportWithIssues(*issues)
        val secondReport = createReportWithIssues(*reversedIssues)

        assertThat(firstReport).hasSameTextualContentAs(secondReport)
    }
}

private fun createTestDetektionWithMultipleSmells(): Detektion {
    val entity1 = createEntity(
        location = createLocation("src/main/com/sample/Sample1.kt", position = 11 to 1, text = 10..14),
    )
    val entity2 = createEntity(
        location = createLocation("src/main/com/sample/Sample2.kt", position = 22 to 2, text = 10..14),
    )
    val entity3 = createEntity(
        location = createLocation("src/main/com/sample/Sample3.kt", position = 33 to 3, text = 10..14),
    )

    return TestDetektion(
        createIssue(createRuleInstance("rule_a/id", "RuleSet1"), entity1, "Issue message 1"),
        createIssue(createRuleInstance("rule_a/id", "RuleSet1"), entity2, "Issue message 2"),
        createIssue(createRuleInstance("rule_b", "RuleSet2"), entity3, "Issue message 3"),
        createIssue(
            createRuleInstance("rule_c", "RuleSet2"),
            entity3,
            "Issue message 3",
            suppressReasons = listOf("suppressed")
        ),
    )
}

private fun issues(): Array<Issue> {
    val entity1 = createEntity(location = createLocation("src/main/com/sample/Sample1.kt", position = 11 to 5))
    val entity2 = createEntity(location = createLocation("src/main/com/sample/Sample1.kt", position = 22 to 2))
    val entity3 = createEntity(location = createLocation("src/main/com/sample/Sample1.kt", position = 11 to 2))
    val entity4 = createEntity(location = createLocation("src/main/com/sample/Sample2.kt", position = 1 to 1))

    return arrayOf(
        createIssue(createRuleInstance("rule_a", "RuleSet1"), entity1),
        createIssue(createRuleInstance("rule_a", "RuleSet1"), entity2),
        createIssue(createRuleInstance("rule_a", "RuleSet1"), entity3),
        createIssue(createRuleInstance("rule_a", "RuleSet1"), entity4),
        createIssue(createRuleInstance("rule_b", "RuleSet1"), entity2),
        createIssue(createRuleInstance("rule_b", "RuleSet1"), entity1),
        createIssue(createRuleInstance("rule_b", "RuleSet1"), entity4),
        createIssue(createRuleInstance("rule_b", "RuleSet2"), entity3),
        createIssue(createRuleInstance("rule_c", "RuleSet2"), entity1),
        createIssue(createRuleInstance("rule_c", "RuleSet2"), entity2),
    )
}

private val generatedRegex = """^generated\swith.*$""".toRegex(RegexOption.MULTILINE)
private const val REPLACEMENT = "generated with..."

private fun createReportWithIssues(vararg issues: Issue): Path {
    val htmlReport = HtmlOutputReport().apply { init(TestSetupContext()) }
    val detektion = TestDetektion(*issues)
    var result = htmlReport.render(detektion)
    result = generatedRegex.replace(result, REPLACEMENT)
    val reportPath = createTempFileForTest("report", ".html")
    reportPath.writeText(result)
    return reportPath
}
