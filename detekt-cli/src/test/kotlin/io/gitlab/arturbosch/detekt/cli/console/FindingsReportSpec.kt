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

        describe("reports the debt per rule set and the overall debt") {
            val expectedContent = readResource("findings-report.txt")
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = mapOf(
                    Pair("TestSmell", listOf(createFinding(), createFinding())),
                    Pair("EmptySmells", emptyList()))
            }
            val output = subject.render(detektion)?.trimEnd()?.decolorized()

            it("has the reference content") {
                assertThat(output).isEqualTo(expectedContent)
            }

            it("does not print rule set ids when no findings of this rule set is found") {
                assertThat(output).doesNotContain("EmptySmells")
            }

            it("does contain the rule set id of rule sets with findings") {
                assertThat(output).contains("TestSmell")
            }
        }

        it("reports no findings") {
            val detektion = TestDetektion()
            assertThat(subject.render(detektion)).isEmpty()
        }
    }
})
