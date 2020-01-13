package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TxtOutputReportTest : Spek({

    describe("TXT output report") {

        it("render none") {
            val report = TxtOutputReport()
            val detektion = TestDetektion()
            val renderedText = ""
            assertThat(report.render(detektion)).isEqualTo(renderedText)
        }

        it("render one") {
            val report = TxtOutputReport()
            val detektion = TestDetektion(createFinding())
            val renderedText = "TestSmell - [TestEntity] at TestFile.kt:1:1 - Signature=S1\n"
            assertThat(report.render(detektion)).isEqualTo(renderedText)
        }

        it("render multiple") {
            val report = TxtOutputReport()
            val detektion = TestDetektion(
                createFinding(ruleName = "TestSmellA"),
                createFinding(ruleName = "TestSmellB"),
                createFinding(ruleName = "TestSmellC"))
            val renderedText = """
                TestSmellA - [TestEntity] at TestFile.kt:1:1 - Signature=S1
                TestSmellB - [TestEntity] at TestFile.kt:1:1 - Signature=S1
                TestSmellC - [TestEntity] at TestFile.kt:1:1 - Signature=S1

            """.trimIndent()
            assertThat(report.render(detektion)).isEqualTo(renderedText)
        }
    }
})
