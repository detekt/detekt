package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.ReportLocator
import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths
import java.util.function.Predicate

/**
 * @author Artur Bosch
 */
internal class ReportsSpec : Spek({

    describe("reports") {

        context("arguments for jcommander") {

            val reportUnderTest = TestOutputReport::class.java.simpleName
            val args = arrayOf(
                    "--report", "xml:/tmp/path1",
                    "--report", "txt:/tmp/path2",
                    "--report", "$reportUnderTest:/tmp/path3",
                    "--report", "html:D:_Gradle\\xxx\\xxx\\build\\reports\\detekt\\detekt.html"
            )
            val (cli, _) = parseArguments<CliArgs>(args)

            val reports = cli.reportPaths

            it("should parse multiple report entries") {
                assertThat(reports).hasSize(4)
            }

            it("it should properly parse XML report entry") {
                val xmlReport = reports[0]
                assertThat(xmlReport.kind).isEqualTo(XmlOutputReport::class.java.simpleName)
                assertThat(xmlReport.path).isEqualTo(Paths.get("/tmp/path1"))
            }

            it("it should properly parse TXT report entry") {
                val txtRepot = reports[1]
                assertThat(txtRepot.kind).isEqualTo(TxtOutputReport::class.java.simpleName)
                assertThat(txtRepot.path).isEqualTo(Paths.get("/tmp/path2"))
            }

            it("it should properly parse custom report entry") {
                val customReport = reports[2]
                assertThat(customReport.kind).isEqualTo(reportUnderTest)
                assertThat(customReport.path).isEqualTo(Paths.get("/tmp/path3"))
            }

            it("it should properly parse HTML report entry") {
                val htmlReport = reports[3]
                assertThat(htmlReport.kind).isEqualTo(HtmlOutputReport::class.java.simpleName)
                assertThat(htmlReport.path).isEqualTo(Paths.get("D:_Gradle\\xxx\\xxx\\build\\reports\\detekt\\detekt.html"))
            }

            val extensions = ReportLocator(ProcessingSettings(listOf())).load()
            val extensionsIds = extensions.mapTo(HashSet()) { it.id }

            it("should be able to convert to output reports") {
                assertThat(reports).allMatch { it.kind in extensionsIds }
            }

            it("should recognize custom output format") {
                assertThat(reports).haveExactly(1,
                        Condition(Predicate { it.kind == reportUnderTest },
                                "Corresponds exactly to the test output report."))

                assertThat(extensions).haveExactly(1,
                        Condition(Predicate { it is TestOutputReport && it.ending == "yml" },
                                "Is exactly the test output report."))
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
