package io.github.detekt.report.txt

import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TxtOutputReportSpec {

    @Test
    fun `renders none`() {
        val report = TxtOutputReport()
        val detektion = TestDetektion()
        val renderedText = ""
        assertThat(report.render(detektion)).isEqualTo(renderedText)
    }

    @Test
    fun `renders one`() {
        val report = TxtOutputReport()
        val detektion = TestDetektion(createFinding())
        val renderedText = "TestSmell - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature\n"
        assertThat(report.render(detektion)).isEqualTo(renderedText)
    }

    @Test
    fun `renders multiple`() {
        val report = TxtOutputReport()
        val detektion = TestDetektion(
            createFinding(ruleName = "TestSmellA"),
            createFinding(ruleName = "TestSmellB"),
            createFinding(ruleName = "TestSmellC")
        )
        val renderedText = """
            TestSmellA - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature
            TestSmellB - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature
            TestSmellC - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature

        """.trimIndent()
        assertThat(report.render(detektion)).isEqualTo(renderedText)
    }
}
