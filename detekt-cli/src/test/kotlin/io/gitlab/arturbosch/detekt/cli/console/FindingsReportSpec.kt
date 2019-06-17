package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.ReportConfig
import io.gitlab.arturbosch.detekt.cli.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FindingsReportSpec : Spek({

    describe("findings report") {

        context("several detekt findings") {

            it("reports the debt per ruleset and the overall debt") {
                val report = FindingsReport().apply {
                    init(ReportConfig())
                }
                val expectedContent = readResource("findings-report.txt")
                val detektion = object : TestDetektion() {
                    override val findings: Map<String, List<Finding>> = mapOf(
                        Pair("TestSmell", listOf(createFinding(), createFinding())),
                        Pair("EmptySmells", emptyList())
                    )
                }
                val output = report.render(detektion).trimEnd().decolorized()
                assertThat(output).isEqualTo(expectedContent)
            }

            it("hides empty ruleset if showProgress = false") {
                val report = FindingsReport().apply {
                    val config = CliArgs { configResource = "/configs/report-without-progress.yml" }.loadConfiguration()
                    init(ReportConfig(config))
                }
                val expectedContent = readResource("findings-report-without-progress.txt")
                val detektion = object : TestDetektion() {
                    override val findings: Map<String, List<Finding>> = mapOf(
                        Pair("TestSmell", listOf(createFinding(), createFinding())),
                        Pair("EmptySmells", emptyList())
                    )
                }
                val output = report.render(detektion).trimEnd().decolorized()
                assertThat(output).isEqualTo(expectedContent)
            }

            it("displays messages if showMessages = true") {
                val report = FindingsReport().apply {
                    val config = CliArgs { configResource = "/configs/report-with-messages.yml" }.loadConfiguration()
                    init(ReportConfig(config))
                }
                val expectedContent = readResource("findings-report-with-messages.txt")
                val detektion = object : TestDetektion() {
                    override val findings: Map<String, List<Finding>> = mapOf(
                        Pair("TestSmell", listOf(createFinding(), createFinding())),
                        Pair("EmptySmells", emptyList())
                    )
                }
                val output = report.render(detektion).trimEnd().decolorized()
                assertThat(output).isEqualTo(expectedContent)
            }

            it("reports no findings") {
                val report = FindingsReport().apply {
                    init(ReportConfig())
                }
                val detektion = TestDetektion()
                assertThat(report.render(detektion)).isEmpty()
            }
        }
    }
})
