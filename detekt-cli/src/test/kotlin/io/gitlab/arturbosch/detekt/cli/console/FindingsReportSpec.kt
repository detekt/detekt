package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FindingsReportSpec : Spek({

    val subject by memoized { FindingsReport() }

    describe("findings report") {

        context("several detekt findings") {

            it("reports the debt per ruleset and the overall debt") {
                val expectedContent = readResource("findings-report.txt")
                val detektion = object : TestDetektion() {
                    override val findings: Map<String, List<Finding>> = mapOf(
                            Pair("TestSmell", listOf(createFinding(), createFinding())),
                            Pair("EmptySmells", emptyList()))
                }
                val output = subject.render(detektion)?.trimEnd()?.decolorized()
                assertThat(output).isEqualTo(expectedContent)
            }

            it("reports no findings") {
                val detektion = TestDetektion()
                assertThat(subject.render(detektion)).isEmpty()
            }
        }
    }
})
