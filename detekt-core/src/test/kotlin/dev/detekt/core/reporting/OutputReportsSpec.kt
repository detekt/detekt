package dev.detekt.core.reporting

import dev.detekt.api.Detektion
import dev.detekt.api.OutputReport
import dev.detekt.core.createNullLoggingSpec
import dev.detekt.core.createProcessingSettings
import dev.detekt.core.tooling.withSettings
import dev.detekt.report.html.HtmlOutputReport
import dev.detekt.report.md.MdOutputReport
import dev.detekt.report.xml.CheckstyleOutputReport
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.dsl.ReportsSpecBuilder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class OutputReportsSpec {

    @Nested
    inner class `arguments for spec` {

        private val reportUnderTest = TestOutputReport::class.java.simpleName
        private val reports = ReportsSpecBuilder().apply {
            report { "checkstyle" to Path("/tmp/path1") }
            report { reportUnderTest to Path("/tmp/path3") }
            report { "html" to Path("D:_Gradle\\xxx\\xxx\\build\\reports\\detekt\\detekt.html") }
            report { "md" to Path("/tmp/path4") }
        }.build().reports.toList()

        @Test
        fun `should parse multiple report entries`() {
            assertThat(reports).hasSize(4)
        }

        @Test
        fun `it should properly parse Checkstyle report entry`() {
            val checkstyleReport = reports[0]
            assertThat(checkstyleReport.type).isEqualTo(defaultReportMapping(CheckstyleOutputReport()))
            assertThat(checkstyleReport.path).isEqualTo(Path("/tmp/path1"))
        }

        @Test
        fun `it should properly parse custom report entry`() {
            val customReport = reports[1]
            assertThat(customReport.type).isEqualTo(reportUnderTest)
            assertThat(customReport.path).isEqualTo(Path("/tmp/path3"))
        }

        @Test
        fun `it should properly parse HTML report entry`() {
            val htmlReport = reports[2]
            assertThat(htmlReport.type).isEqualTo(defaultReportMapping(HtmlOutputReport()))
            assertThat(htmlReport.path).isEqualTo(
                Path("D:_Gradle\\xxx\\xxx\\build\\reports\\detekt\\detekt.html")
            )
        }

        @Test
        fun `it should properly parse MD report entry`() {
            val mdReport = reports[3]
            assertThat(mdReport.type).isEqualTo(defaultReportMapping(MdOutputReport()))
            assertThat(mdReport.path).isEqualTo(Path("/tmp/path4"))
        }

        @Nested
        inner class `default report ids` {

            private val extensions = createProcessingSettings().use { OutputReportLocator(it).load() }
            private val extensionsIds = extensions.mapTo(HashSet()) { defaultReportMapping(it) }

            @Test
            fun `should be able to convert to output reports`() {
                assertThat(reports).allMatch { it.type in extensionsIds }
            }

            @Test
            fun `should recognize custom output format`() {
                assertThat(reports).haveExactly(
                    1,
                    Condition(
                        { it.type == reportUnderTest },
                        "Corresponds exactly to the test output report."
                    )
                )

                assertThat(extensions).haveExactly(
                    1,
                    Condition(
                        { it is TestOutputReport && it.ending == "yml" },
                        "Is exactly the test output report."
                    )
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

            val extensions = spec.withSettings { ConsoleReportLocator(this).load() }

            assertThat(extensions).isEmpty()
        }
    }
}

class TestOutputReport : OutputReport {

    override val id: String = "TestOutputReport"
    override val ending: String = "yml"

    override fun render(detektion: Detektion) = throw UnsupportedOperationException("not implemented")
}
