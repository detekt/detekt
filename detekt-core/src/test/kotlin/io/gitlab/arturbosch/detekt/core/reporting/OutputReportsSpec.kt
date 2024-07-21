package io.gitlab.arturbosch.detekt.core.reporting

import io.github.detekt.report.html.HtmlOutputReport
import io.github.detekt.report.md.MdOutputReport
import io.github.detekt.report.xml.XmlOutputReport
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.dsl.ReportsSpecBuilder
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import io.gitlab.arturbosch.detekt.core.createProcessingSettings
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
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
            report { "xml" to Path("/tmp/path1") }
            report { reportUnderTest to Path("/tmp/path3") }
            report { "html" to Path("D:_Gradle\\xxx\\xxx\\build\\reports\\detekt\\detekt.html") }
            report { "md" to Path("/tmp/path4") }
        }.build().reports.toList()

        @Test
        fun `should parse multiple report entries`() {
            assertThat(reports).hasSize(4)
        }

        @Test
        fun `it should properly parse XML report entry`() {
            val xmlReport = reports[0]
            assertThat(xmlReport.type).isEqualTo(defaultReportMapping(XmlOutputReport()))
            assertThat(xmlReport.path).isEqualTo(Path("/tmp/path1"))
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

class TestOutputReport : OutputReport() {

    override val id: String = "TestOutputReport"
    override val ending: String = "yml"

    override fun render(detektion: Detektion): String? = throw UnsupportedOperationException("not implemented")
}
