package io.github.detekt.report.html

import io.github.detekt.metrics.CognitiveComplexity
import io.github.detekt.metrics.processors.commentLinesKey
import io.github.detekt.metrics.processors.complexityKey
import io.github.detekt.metrics.processors.linesKey
import io.github.detekt.metrics.processors.logicalLinesKey
import io.github.detekt.metrics.processors.sourceLinesKey
import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.internal.FakeKtElement
import io.github.detekt.test.utils.internal.FakePsiFile
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createFinding
import io.gitlab.arturbosch.detekt.test.createLocation
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtElement
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.writeText

class HtmlOutputReportSpec {

    private val htmlReport = HtmlOutputReport()

    @Test
    fun `renders the HTML headers correctly`() {
        val result = htmlReport.render(TestDetektion())

        assertThat(result).startsWith("<!DOCTYPE html>\n<html lang=\"en\">")
        assertThat(result).endsWith("</html>\n")

        assertThat(result).contains("<h2>Metrics</h2>")
        assertThat(result).contains("<h2>Complexity Report</h2>")
        assertThat(result).contains("<h2>Findings</h2>")
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
    fun `contains the total number of findings`() {
        val result = htmlReport.render(createTestDetektionWithMultipleSmells())

        assertThat(result).contains("Total: 3")
    }

    @Test
    fun `contains no findings`() {
        val detektion = object : TestDetektion() {
            override val findings: Map<RuleSet.Id, List<Finding2>> = mapOf(
                RuleSet.Id("EmptyRuleset") to emptyList()
            )
        }
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
    fun `renders the right file locations for relative paths`() {
        val result = htmlReport.render(createTestDetektionFromRelativePath())

        assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample1.kt:11:1</span>")
        assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample2.kt:22:2</span>")
        assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample3.kt:33:3</span>")
    }

    @Test
    fun `renders the right number of issues per rule`() {
        val result = htmlReport.render(createTestDetektionWithMultipleSmells())

        assertThat(result).contains("<span class=\"rule\">id_a: 2 </span>")
        assertThat(result).contains("<span class=\"rule\">id_b: 1 </span>")
    }

    @Test
    fun `renders the right violation messages for the rules`() {
        val result = htmlReport.render(createTestDetektionWithMultipleSmells())

        assertThat(result).contains("<span class=\"message\">Message finding 1</span>")
        assertThat(result).contains("<span class=\"message\">Message finding 2</span>")
        assertThat(result).doesNotContain("<span class=\"message\"></span>")
    }

    @Test
    fun `renders the right violation description for the rules`() {
        val result = htmlReport.render(createTestDetektionWithMultipleSmells())

        assertThat(result).contains("<span class=\"description\">Description id_a</span>")
        assertThat(result).contains("<span class=\"description\">Description id_b</span>")
    }

    @Test
    fun `renders the right documentation links for the rules`() {
        val detektion = object : TestDetektion() {
            override val findings: Map<RuleSet.Id, List<Finding2>> = mapOf(
                RuleSet.Id("Style") to listOf(
                    createFinding("ValCouldBeVar")
                ),
                RuleSet.Id("empty") to listOf(
                    createFinding("EmptyBody"),
                    createFinding("EmptyIf")
                )
            )
        }

        val result = htmlReport.render(detektion)
        assertThat(result).contains("<a href=\"https://detekt.dev/docs/rules/style#valcouldbevar\">Documentation</a>")
        assertThat(result).contains("<a href=\"https://detekt.dev/docs/rules/empty#emptybody\">Documentation</a>")
        assertThat(result).contains("<a href=\"https://detekt.dev/docs/rules/empty#emptyif\">Documentation</a>")
    }

    @Test
    fun `renders a metric report correctly`() {
        val detektion = object : TestDetektion() {
            override val metrics: Collection<ProjectMetric> = listOf(
                ProjectMetric("M1", 10_000),
                ProjectMetric("M2", 2)
            )
        }
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
        val expected = resourceAsPath("HtmlOutputFormatTest.html")
        var result = htmlReport.render(createTestDetektionWithMultipleSmells())
        result = generatedRegex.replace(result, REPLACEMENT)

        val actual = createTempFileForTest("actual-report", ".html")
        actual.writeText(result)

        assertThat(actual).hasSameTextualContentAs(expected)
    }

    @Test
    fun `asserts that the generated HTML is the same even if we change the order of the findings`() {
        val findings = findings()
        val reversedFindings = findings
            .reversedArray()
            .map { (section, findings) -> section to findings.asReversed() }
            .toTypedArray()

        val firstReport = createReportWithFindings(findings)
        val secondReport = createReportWithFindings(reversedFindings)

        assertThat(firstReport).hasSameTextualContentAs(secondReport)
    }
}

private fun fakeKtElement(): KtElement {
    val code = "\n\n\n\n\n\n\n\n\n\nabcdef\nhi\n"
    val fakePsiFile = FakePsiFile(code)
    val fakeKtElement = FakeKtElement(fakePsiFile)

    return fakeKtElement
}

private fun createTestDetektionWithMultipleSmells(): Detektion {
    val entity1 = createEntity(
        location = createLocation("src/main/com/sample/Sample1.kt", position = 11 to 1, text = 10..14),
        ktElement = fakeKtElement()
    )
    val entity2 = createEntity(location = createLocation("src/main/com/sample/Sample2.kt", position = 22 to 2))
    val entity3 = createEntity(location = createLocation("src/main/com/sample/Sample3.kt", position = 33 to 3))

    return createHtmlDetektion(
        "RuleSet1" to listOf(
            createFinding("id_a", entity1, "Message finding 1"),
            createFinding("id_a", entity2, "Message finding 2")
        ),
        "RuleSet2" to listOf(createFinding("id_b", entity3, "Message finding 3"))
    )
}

private fun createTestDetektionFromRelativePath(): Detektion {
    val entity1 = createEntity(
        location = createLocation(
            path = "src/main/com/sample/Sample1.kt",
            basePath = "/Users/tester/detekt/",
            position = 11 to 1,
            text = 10..14,
        ),
        ktElement = fakeKtElement(),
    )
    val entity2 = createEntity(
        location = createLocation(
            path = "src/main/com/sample/Sample2.kt",
            basePath = "/Users/tester/detekt/",
            position = 22 to 2,
        )
    )
    val entity3 = createEntity(
        location = createLocation(
            path = "src/main/com/sample/Sample3.kt",
            basePath = "/Users/tester/detekt/",
            position = 33 to 3,
        )
    )

    return createHtmlDetektion(
        "RuleSet1" to listOf(
            createFinding("id_a", entity1, "Message finding 1"),
            createFinding("id_a", entity2, "Message finding 2")
        ),
        "RuleSet2" to listOf(createFinding("id_b", entity3, "Message finding 3"))
    )
}

private fun findings(): Array<Pair<String, List<Finding2>>> {
    val entity1 = createEntity(location = createLocation("src/main/com/sample/Sample1.kt", position = 11 to 5))
    val entity2 = createEntity(location = createLocation("src/main/com/sample/Sample1.kt", position = 22 to 2))
    val entity3 = createEntity(location = createLocation("src/main/com/sample/Sample1.kt", position = 11 to 2))
    val entity4 = createEntity(location = createLocation("src/main/com/sample/Sample2.kt", position = 1 to 1))

    return arrayOf(
        "RuleSet1" to listOf(
            createFinding("id_a", entity1),
            createFinding("id_a", entity2),
            createFinding("id_a", entity3),
            createFinding("id_a", entity4),
            createFinding("id_b", entity2),
            createFinding("id_b", entity1),
            createFinding("id_b", entity4)
        ),
        "RuleSet2" to listOf(
            createFinding("id_b", entity3),
            createFinding("id_c", entity1),
            createFinding("id_c", entity2)
        )
    )
}

private fun createHtmlDetektion(vararg findingPairs: Pair<String, List<Finding2>>): Detektion {
    return object : TestDetektion() {
        override val findings: Map<RuleSet.Id, List<Finding2>> = findingPairs.toMap()
            .mapKeys { (key, _) -> RuleSet.Id(key) }
    }
}

private val generatedRegex = """^generated\swith.*$""".toRegex(RegexOption.MULTILINE)
private const val REPLACEMENT = "generated with..."

private fun createReportWithFindings(findings: Array<Pair<String, List<Finding2>>>): Path {
    val htmlReport = HtmlOutputReport()
    val detektion = createHtmlDetektion(*findings)
    var result = htmlReport.render(detektion)
    result = generatedRegex.replace(result, REPLACEMENT)
    val reportPath = createTempFileForTest("report", ".html")
    reportPath.writeText(result)
    return reportPath
}
