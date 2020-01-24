package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.core.processors.commentLinesKey
import io.gitlab.arturbosch.detekt.core.processors.complexityKey
import io.gitlab.arturbosch.detekt.core.processors.logicalLinesKey
import io.gitlab.arturbosch.detekt.core.processors.sourceLinesKey
import io.gitlab.arturbosch.detekt.core.processors.linesKey
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.resource
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtElement
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Paths

class HtmlOutputFormatTest : Spek({

    describe("HTML output format") {
        val outputFormat = HtmlOutputReport()

        it("testRenderResultLooksLikeHtml") {
            val result = outputFormat.render(TestDetektion())

            assertThat(result).startsWith("<!DOCTYPE html>\n<html lang=\"en\">")
            assertThat(result).endsWith("</html>\n")

            assertThat(result).contains("<h1>detekt report</h1>")
            assertThat(result).contains("<h2>Metrics</h2>")
            assertThat(result).contains("<h2>Complexity Report</h2>")
            assertThat(result).contains("<h2>Findings</h2>")
        }

        it("testRenderResultContainsFileLocations") {
            val result = outputFormat.render(createTestDetektionWithMultipleSmells())

            assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample1.kt:11:1</span>")
            assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample2.kt:22:2</span>")
            assertThat(result).contains("<span class=\"location\">src/main/com/sample/Sample3.kt:33:3</span>")
        }

        it("testRenderResultContainsRules") {
            val result = outputFormat.render(createTestDetektionWithMultipleSmells())

            assertThat(result).contains("<span class=\"rule\">id_a </span>")
            assertThat(result).contains("<span class=\"rule\">id_c </span>")
        }

        it("testRenderResultContainsMessages") {
            val result = outputFormat.render(createTestDetektionWithMultipleSmells())

            assertThat(result).contains("<span class=\"message\">B1</span>")
            assertThat(result).contains("<span class=\"message\">B2</span>")
        }

        it("testRenderResultContainsDescriptions") {
            val result = outputFormat.render(createTestDetektionWithMultipleSmells())

            assertThat(result).contains("<span class=\"description\">A1</span>")
            assertThat(result).doesNotContain("<span class=\"description\">A2</span>")
            assertThat(result).contains("<span class=\"description\">A3</span>")
        }

        it("testRenderResultContainingAtLeastOneMetric") {
            val detektion = object : TestDetektion() {
                override val metrics: Collection<ProjectMetric> = listOf(
                    ProjectMetric("M1", 10000), ProjectMetric("M2", 2)
                )
            }
            val result = outputFormat.render(detektion)
            assertThat(result).contains("<li>M1: 10,000</li>")
            assertThat(result).contains("<li>M2: 2</li>")
        }

        it("testRenderResultContainingComplexityReport") {
            val detektion = TestDetektion()
            detektion.addData(complexityKey, 10)
            detektion.addData(sourceLinesKey, 20)
            detektion.addData(logicalLinesKey, 10)
            detektion.addData(commentLinesKey, 2)
            detektion.addData(linesKey, 22)
            val result = outputFormat.render(detektion)
            assertThat(result).contains("<li>22 lines of code (loc)</li>")
            assertThat(result).contains("<li>20 source lines of code (sloc)</li>")
            assertThat(result).contains("<li>10 logical lines of code (lloc)</li>")
        }

        it("testRenderResultWithBlankComplexityReport") {
            val result = outputFormat.render(createTestDetektionWithMultipleSmells())
            assertThat(result).contains("<h2>Complexity Report</h2>\n\n<div>\n  <ul></ul>\n</div>")
        }

        it("assert that the html generated is the expected") {
            val result = outputFormat.render(createTestDetektionWithMultipleSmells())

            val tmpReport = Files.createTempFile("HtmlOutputFormatTest", ".html")
            Files.write(tmpReport, result.toByteArray())

            try {
                assertThat(tmpReport).hasSameContentAs(Paths.get(resource("/reports/HtmlOutputFormatTest.html")))
            } finally {
                Files.delete(tmpReport)
            }
        }
    }
})

private fun createTestDetektionWithMultipleSmells(): Detektion {
    val ktElementMock = mockk<KtElement>()
    val psiFileMock = mockk<PsiFile>()
    every { psiFileMock.text } returns "\n\n\n\n\n\n\n\n\n\nabcdef\nhi\n"
    every { ktElementMock.containingFile } returns psiFileMock

    val entity1 = Entity("Sample1", "com.sample.Sample1", "",
            Location(
                SourceLocation(11, 1), TextLocation(10, 14),
                    "abcd", "src/main/com/sample/Sample1.kt"), ktElementMock
    )
    val entity2 = Entity("Sample2", "com.sample.Sample2", "",
            Location(SourceLocation(22, 2), TextLocation(0, 20),
                    "efgh", "src/main/com/sample/Sample2.kt"))
    val entity3 = Entity("Sample3", "com.sample.Sample3", "",
            Location(SourceLocation(33, 3), TextLocation(0, 30),
                    "ijkl", "src/main/com/sample/Sample3.kt"))

    return TestDetektion(
        CodeSmell(Issue("id_a", Severity.CodeSmell, "A1", Debt.TWENTY_MINS), entity1, message = "B1"),
        CodeSmell(Issue("id_a", Severity.CodeSmell, "A2", Debt.TWENTY_MINS), entity2, message = "B2"),
        CodeSmell(Issue("id_c", Severity.CodeSmell, "A3", Debt.TWENTY_MINS), entity3, message = "")
    )
}
