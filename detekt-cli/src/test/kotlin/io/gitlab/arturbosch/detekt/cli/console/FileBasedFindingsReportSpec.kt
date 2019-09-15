package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FileBasedFindingsReportSpec : Spek({

    val subject by memoized { FileBasedFindingsReport() }

    describe("findings report") {

        describe("reports the debt per file and rule set with the overall debt") {
            val expectedContent = readResource("grouped-findings-report.txt")
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = mapOf(
                    Pair(
                        "TestSmell",
                        listOf(
                            createFinding(ruleSet = "NewRule", fileName = "AnotherFile.kt"),
                            createFinding(ruleSet = "NewRule", fileName = "TestFile.kt"),
                            createFinding(ruleSet = "RandomRule", fileName = "AnotherFile.kt")
                        )
                    ),
                    Pair(
                        "AnotherTestSmell",
                        listOf(
                            createFinding(ruleSet = "AnotherRule", fileName = "AnotherFile.kt"),
                            createFinding(ruleSet = "AnotherRandomRule", fileName = "TestFile.kt")
                        )
                    ),
                    Pair("EmptySmells", emptyList())
                )
            }
            val output = subject.render(detektion)?.trimEnd()?.decolorized()

            it("has the reference content") {
                assertThat(output).isEqualTo(expectedContent)
            }

        }

        it("reports no findings") {
            val detektion = TestDetektion()
            assertThat(subject.render(detektion)).isNull()
        }

        it("reports no findings when no rule set contains smells") {
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = mapOf(
                    Pair("EmptySmells", emptyList())
                )
            }
            assertThat(subject.render(detektion)).isNull()
        }
    }
})
