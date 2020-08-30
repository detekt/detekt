@file:Suppress("MaxLineLength")

package io.github.detekt.report.xml

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.test.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class XmlOutputFormatSpec : Spek({

    val entity1 by memoized {
        Entity("Sample1", "",
            Location(SourceLocation(11, 1), TextLocation(0, 10),
                "src/main/com/sample/Sample1.kt"))
    }
    val entity2 by memoized {
        Entity("Sample2", "",
            Location(SourceLocation(22, 2), TextLocation(0, 20),
                "src/main/com/sample/Sample2.kt"))
    }

    val outputFormat by memoized { XmlOutputReport() }

    describe("XML output format") {

        it("renderEmpty") {
            val result = outputFormat.render(TestDetektion())

            assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n</checkstyle>")
        }

        it("renderOneForSingleFile") {
            val smell = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")

            val result = outputFormat.render(TestDetektion(smell))

            assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n</file>\n</checkstyle>")
        }

        it("renderTwoForSingleFile") {
            val smell1 = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")
            val smell2 = CodeSmell(Issue("id_b", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")

            val result = outputFormat.render(TestDetektion(smell1, smell2))

            assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_b\" />\n</file>\n</checkstyle>")
        }

        it("renderOneForMultipleFiles") {
            val smell1 = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")
            val smell2 = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity2, message = "")

            val result = outputFormat.render(TestDetektion(smell1, smell2))

            assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n</file>\n<file name=\"src/main/com/sample/Sample2.kt\">\n\t<error line=\"22\" column=\"2\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n</file>\n</checkstyle>")
        }

        it("renderTwoForMultipleFiles") {
            val smell1 = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")
            val smell2 = CodeSmell(Issue("id_b", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")
            val smell3 = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity2, message = "")
            val smell4 = CodeSmell(Issue("id_b", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity2, message = "")

            val result = outputFormat.render(
                TestDetektion(
                    smell1,
                    smell2,
                    smell3,
                    smell4
                )
            )

            assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_b\" />\n</file>\n<file name=\"src/main/com/sample/Sample2.kt\">\n\t<error line=\"22\" column=\"2\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n\t<error line=\"22\" column=\"2\" severity=\"warning\" message=\"\" source=\"detekt.id_b\" />\n</file>\n</checkstyle>")
        }

        describe("severities conversion") {

            Severity.values().forEach { severity ->

                val severityLabel = when (severity) {
                    Severity.CodeSmell,
                    Severity.Style,
                    Severity.Warning,
                    Severity.Maintainability,
                    Severity.Performance -> "warning"
                    Severity.Defect -> "error"
                    Severity.Minor -> "info"
                    Severity.Security -> "fatal"
                }

                it("renders detektion with severity [${severity.name}] as XML with severity [$severityLabel]") {
                    val finding = CodeSmell(
                        issue = Issue("issue_id", severity, "issue description", Debt.FIVE_MINS),
                        message = "message",
                        entity = entity1
                    )

                    val expected = """
                    <?xml version="1.0" encoding="utf-8"?>
                    <checkstyle version="4.3">
                    <file name="${finding.location.file}">
                    ${"\t"}<error line="${finding.location.source.line}" column="${finding.location.source.column}" severity="$severityLabel" message="${finding.messageOrDescription()}" source="detekt.${finding.id}" />
                    </file>
                    </checkstyle>
                    """

                    val actual = outputFormat.render(TestDetektion(finding))

                    assertThat(actual).isEqualTo(expected.trimIndent())
                }
            }
        }
    }
})
