package io.github.detekt.report.txt

import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TxtOutputReportSpec : Spek({

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
            val renderedText = "TestSmell - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature\n"
            assertThat(report.render(detektion)).isEqualTo(renderedText)
        }

        it("render multiple") {
            val report = TxtOutputReport()
            val detektion = TestDetektion(
                createFinding(ruleName = "TestSmellA"),
                createFinding(ruleName = "TestSmellB"),
                createFinding(ruleName = "TestSmellC"))
            val renderedText = """
                TestSmellA - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature
                TestSmellB - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature
                TestSmellC - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature

            """.trimIndent()
            assertThat(report.render(detektion)).isEqualTo(renderedText)
        }
    }
})
