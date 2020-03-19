package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.cli.createEntity
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.gitlab.arturbosch.detekt.cli.createIssue
import io.gitlab.arturbosch.detekt.core.processors.commentLinesKey
import io.gitlab.arturbosch.detekt.core.processors.complexityKey
import io.gitlab.arturbosch.detekt.core.processors.linesKey
import io.gitlab.arturbosch.detekt.core.processors.logicalLinesKey
import io.gitlab.arturbosch.detekt.core.processors.sourceLinesKey
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.resource
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap
import org.jetbrains.kotlin.psi.KtElement
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Paths

class HtmlOutputReportSpec : Spek({

    describe("HTML output report") {
        val htmlReport = HtmlOutputReport()

        it("renders the HTML headers correctly") {
            val result = htmlReport.render(TestDetektion())

            assertThat(result).startsWith("<!DOCTYPE html>\n<html lang=\"en\">")
            assertThat(result).endsWith("</html>\n")

            assertThat(result).contains("<h1>detekt report</h1>")
            assertThat(result).contains("<h2>Metrics</h2>")
            assertThat(result).contains("<h2>Complexity Report</h2>")
            assertThat(result).contains("<h2>Findings</h2>")
        }

        it("contains the total number of findings") {
            val result = htmlReport.render(createTestDetektionWithMultipleSmells())

            assertThat(result).contains("Total: 3")
        }

        it("contains no findings") {
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = mapOf(
                    Pair("EmptyRuleset", emptyList())
                )
            }
            val result = htmlReport.render(detektion)
            assertThat(result).contains("Total: 0")
        }

        it("renders the right file locations") {
            val result = htmlReport.render(createTestDetektionWithMultipleSmells())

            assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample1.kt:11:1</span>")
            assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample2.kt:22:2</span>")
            assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample3.kt:33:3</span>")
        }

        it("renders the right number of issues per rule") {
            val result = htmlReport.render(createTestDetektionWithMultipleSmells())

            assertThat(result).contains("<span class=\"rule\">id_a: 2 </span>")
            assertThat(result).contains("<span class=\"rule\">id_b: 1 </span>")
        }

        it("renders the right violation messages for the rules") {
            val result = htmlReport.render(createTestDetektionWithMultipleSmells())

            assertThat(result).contains("<span class=\"message\">Message finding 1</span>")
            assertThat(result).contains("<span class=\"message\">Message finding 2</span>")
            assertThat(result).doesNotContain("<span class=\"message\"></span>")
        }

        it("renders the right violation description for the rules") {
            val result = htmlReport.render(createTestDetektionWithMultipleSmells())

            assertThat(result).contains("<span class=\"description\">Description id_a</span>")
            assertThat(result).contains("<span class=\"description\">Description id_b</span>")
        }

        it("renders a metric report correctly") {
            val detektion = object : TestDetektion() {
                override val metrics: Collection<ProjectMetric> = listOf(
                    ProjectMetric("M1", 10000), ProjectMetric("M2", 2)
                )
            }
            val result = htmlReport.render(detektion)
            assertThat(result).contains("<li>10,000 M1</li>")
            assertThat(result).contains("<li>2 M2</li>")
        }

        it("renders the complexity report correctly") {
            val detektion = TestDetektion()
            detektion.addData(complexityKey, 10)
            detektion.addData(sourceLinesKey, 20)
            detektion.addData(logicalLinesKey, 10)
            detektion.addData(commentLinesKey, 2)
            detektion.addData(linesKey, 2222)
            val result = htmlReport.render(detektion)
            assertThat(result).contains("<li>2,222 lines of code (loc)</li>")
            assertThat(result).contains("<li>20 source lines of code (sloc)</li>")
            assertThat(result).contains("<li>10 logical lines of code (lloc)</li>")
        }

        it("renders a blank complexity report correctly") {
            val result = htmlReport.render(createTestDetektionWithMultipleSmells())
            assertThat(result).contains("<h2>Complexity Report</h2>\n\n<div>\n  <ul></ul>\n</div>")
        }

        it("asserts that the generated HTML is the same as expected") {
            val result = htmlReport.render(createTestDetektionWithMultipleSmells())

            val tmpReport = Files.createTempFile("HtmlOutputFormatTest", ".html")
            Files.write(tmpReport, result.toByteArray())

            try {
                assertThat(tmpReport).hasSameTextualContentAs(Paths.get(resource("/reports/HtmlOutputFormatTest.html")))
            } finally {
                Files.delete(tmpReport)
            }
        }

        it("asserts that the generated HTML is the same even if we change the order of the findings") {
            val findings = findings()
            val reversedFindings = findings
                .reversedArray()
                .map { (section, findings) -> section to findings.asReversed() }
                .toTypedArray()

            val result1 = htmlReport.render(HtmlDetektion(*findings))
            val result2 = htmlReport.render(HtmlDetektion(*reversedFindings))

            val tmpReport1 = Files.createTempFile("HtmlOutputFormatTest", ".html")
            val tmpReport2 = Files.createTempFile("HtmlOutputFormatTest", ".html")
            Files.write(tmpReport1, result1.toByteArray())
            Files.write(tmpReport2, result2.toByteArray())

            try {
                assertThat(tmpReport1).hasSameTextualContentAs(tmpReport2)
            } finally {
                Files.delete(tmpReport1)
                Files.delete(tmpReport2)
            }
        }
    }
})

private fun createTestDetektionWithMultipleSmells(): Detektion {
    val ktElementMock = mockk<KtElement>()
    val psiFileMock = mockk<PsiFile>()
    every { psiFileMock.text } returns "\n\n\n\n\n\n\n\n\n\nabcdef\nhi\n"
    every { ktElementMock.containingFile } returns psiFileMock

    val entity1 = createEntity("src/main/com/sample/Sample1.kt", 11 to 1, 10..14, ktElementMock)
    val entity2 = createEntity("src/main/com/sample/Sample2.kt", 22 to 2)
    val entity3 = createEntity("src/main/com/sample/Sample3.kt", 33 to 3)

    val issueA = createIssue("id_a")
    val issueB = createIssue("id_b")

    return HtmlDetektion(
        "Section 1" to listOf(
            createFinding(issueA, entity1, "Message finding 1"),
            createFinding(issueA, entity2, "Message finding 2")
        ),
        "Section 2" to listOf(createFinding(issueB, entity3, ""))
    )
}

private fun findings(): Array<Pair<String, List<Finding>>> {
    val issueA = createIssue("id_a")
    val issueB = createIssue("id_b")
    val issueC = createIssue("id_c")

    val entity1 = createEntity("src/main/com/sample/Sample1.kt", 11 to 5)
    val entity2 = createEntity("src/main/com/sample/Sample1.kt", 22 to 2)
    val entity3 = createEntity("src/main/com/sample/Sample1.kt", 11 to 2)
    val entity4 = createEntity("src/main/com/sample/Sample2.kt", 1 to 1)

    return arrayOf(
        "Section 1" to listOf(
            createFinding(issueA, entity1),
            createFinding(issueA, entity2),
            createFinding(issueA, entity3),
            createFinding(issueA, entity4),
            createFinding(issueB, entity2),
            createFinding(issueB, entity1),
            createFinding(issueB, entity4)
        ),
        "Section 2" to listOf(
            createFinding(issueB, entity3),
            createFinding(issueC, entity1),
            createFinding(issueC, entity2)
        )
    )
}

private class HtmlDetektion(vararg findings: Pair<String, List<Finding>>) : Detektion {
    override val metrics: Collection<ProjectMetric> = listOf()
    override val findings: Map<String, List<Finding>> = findings.toMap()
    override val notifications: List<Notification> = listOf()
    private var userData = KeyFMap.EMPTY_MAP

    override fun <V> getData(key: Key<V>): V? = userData.get(key)
    override fun <V> addData(key: Key<V>, value: V) {
        userData = userData.plus(key, value)
    }
    override fun add(notification: Notification) = throw UnsupportedOperationException("not implemented")
    override fun add(projectMetric: ProjectMetric) = throw UnsupportedOperationException("not implemented")
}
