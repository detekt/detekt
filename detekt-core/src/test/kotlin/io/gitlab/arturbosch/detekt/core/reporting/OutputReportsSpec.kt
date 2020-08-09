package io.gitlab.arturbosch.detekt.core.reporting

import io.github.detekt.report.html.HtmlOutputReport
import io.github.detekt.report.txt.TxtOutputReport
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
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths
import java.util.function.Predicate

internal class OutputReportsSpec : Spek({

    describe("reports") {

        context("arguments for spec") {

            val reportUnderTest by memoized { TestOutputReport::class.java.simpleName }
            val reports by memoized {
                ReportsSpecBuilder().apply {
                    report { "xml" to Paths.get("/tmp/path1") }
                    report { "txt" to Paths.get("/tmp/path2") }
                    report { reportUnderTest to Paths.get("/tmp/path3") }
                    report { "html" to Paths.get("D:_Gradle\\xxx\\xxx\\build\\reports\\detekt\\detekt.html") }
                }.build().reports.toList()
            }

            it("should parse multiple report entries") {
                assertThat(reports).hasSize(4)
            }

            it("it should properly parse XML report entry") {
                val xmlReport = reports[0]
                assertThat(xmlReport.type).isEqualTo(defaultReportMapping(XmlOutputReport::class.java.simpleName))
                assertThat(xmlReport.path).isEqualTo(Paths.get("/tmp/path1"))
            }

            it("it should properly parse TXT report entry") {
                val txtRepot = reports[1]
                assertThat(txtRepot.type).isEqualTo(defaultReportMapping(TxtOutputReport::class.java.simpleName))
                assertThat(txtRepot.path).isEqualTo(Paths.get("/tmp/path2"))
            }

            it("it should properly parse custom report entry") {
                val customReport = reports[2]
                assertThat(customReport.type).isEqualTo(reportUnderTest)
                assertThat(defaultReportMapping(customReport.type)).isEqualTo(reportUnderTest)
                assertThat(customReport.path).isEqualTo(Paths.get("/tmp/path3"))
            }

            it("it should properly parse HTML report entry") {
                val htmlReport = reports[3]
                assertThat(htmlReport.type).isEqualTo(defaultReportMapping(HtmlOutputReport::class.java.simpleName))
                assertThat(htmlReport.path).isEqualTo(
                    Paths.get("D:_Gradle\\xxx\\xxx\\build\\reports\\detekt\\detekt.html")
                )
            }

            context("default report ids") {

                val extensions by memoized { createProcessingSettings().use { OutputReportLocator(it).load() } }
                val extensionsIds by memoized { extensions.mapTo(HashSet()) { defaultReportMapping(it.id) } }

                it("should be able to convert to output reports") {
                    assertThat(reports).allMatch { it.type in extensionsIds }
                }

                it("should recognize custom output format") {
                    assertThat(reports).haveExactly(1,
                        Condition(Predicate { it.type == reportUnderTest },
                            "Corresponds exactly to the test output report."))

                    assertThat(extensions).haveExactly(1,
                        Condition(Predicate { it is TestOutputReport && it.ending == "yml" },
                            "Is exactly the test output report."))
                }
            }
        }

        context("empty reports") {

            it("yields empty extension list") {
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
})

internal class TestOutputReport : OutputReport() {
    override val ending: String = "yml"
    override fun render(detektion: Detektion): String? {
        throw UnsupportedOperationException("not implemented")
    }
}
