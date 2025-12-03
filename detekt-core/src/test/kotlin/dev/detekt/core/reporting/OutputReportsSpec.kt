package dev.detekt.core.reporting

import dev.detekt.api.Detektion
import dev.detekt.api.OutputReport
import dev.detekt.core.createNullLoggingSpec
import dev.detekt.core.createProcessingSettings
import dev.detekt.core.extensions.loadExtensions
import dev.detekt.core.tooling.withSettings
import dev.detekt.report.html.HtmlOutputReport
import dev.detekt.report.markdown.MarkdownOutputReport
import dev.detekt.report.xml.CheckstyleOutputReport
import dev.detekt.test.resourceAsPath
import dev.detekt.tooling.dsl.ReportsSpecBuilder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class OutputReportsSpec {

    @Nested
    inner class `arguments for spec` {

        private val reports = ReportsSpecBuilder().apply {
            report { "checkstyle" to Path("/tmp/path1") }
            report { "yml" to Path("/tmp/path3") }
            report { "html" to Path("D:_Gradle\\xxx\\xxx\\build\\reports\\detekt\\detekt.html") }
            report { "markdown" to Path("/tmp/path4") }
        }.build().reports.toList()

        @Test
        fun `should parse multiple report entries`() {
            assertThat(reports).hasSize(4)
        }

        @Test
        fun `it should properly parse Checkstyle report entry`() {
            val checkstyleReport = reports[0]
            assertThat(checkstyleReport.type).isEqualTo(CheckstyleOutputReport().id)
            assertThat(checkstyleReport.path).isEqualTo(Path("/tmp/path1"))
        }

        @Test
        fun `it should properly parse custom report entry`() {
            val customReport = reports[1]
            assertThat(customReport.type).isEqualTo(TestOutputReport().id)
            assertThat(customReport.path).isEqualTo(Path("/tmp/path3"))
        }

        @Test
        fun `it should properly parse HTML report entry`() {
            val htmlReport = reports[2]
            assertThat(htmlReport.type).isEqualTo(HtmlOutputReport().id)
            assertThat(htmlReport.path).isEqualTo(
                Path("D:_Gradle\\xxx\\xxx\\build\\reports\\detekt\\detekt.html")
            )
        }

        @Test
        fun `it should properly parse Markdown report entry`() {
            val markdownReport = reports[3]
            assertThat(markdownReport.type).isEqualTo(MarkdownOutputReport().id)
            assertThat(markdownReport.path).isEqualTo(Path("/tmp/path4"))
        }

        @Nested
        inner class `default report ids` {
            private val extensions = createProcessingSettings().use { loadExtensions<OutputReport>(it) }
            private val extensionsIds = extensions.mapTo(HashSet()) { it.id }

            @Test
            fun `should be able to convert to output reports`() {
                assertThat(reports).allMatch { it.type in extensionsIds }
            }

            @Test
            fun `should recognize custom output format`() {
                assertThat(reports).haveExactly(
                    1,
                    Condition({ it.type == "yml" }, "Corresponds exactly to the test output report.")
                )
            }
        }
    }

    @Nested
    inner class `empty reports` {

        @Test
        fun `yields empty extension list`() {
            val spec = createNullLoggingSpec {
                config {
                    configPaths = listOf(resourceAsPath("/reporting/disabled-reports.yml"))
                }
            }

            val extensions = spec.withSettings { loadConsoleReport(this) }

            assertThat(extensions).isEmpty()
        }
    }
}

class TestOutputReport : OutputReport {
    override val id: String = "yml"

    override fun render(detektion: Detektion) = throw UnsupportedOperationException("not implemented")
}
