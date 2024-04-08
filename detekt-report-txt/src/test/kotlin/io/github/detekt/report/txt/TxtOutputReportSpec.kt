package io.github.detekt.report.txt

import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createIssue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TxtOutputReportSpec {

    @Test
    fun `renders none`() {
        val detektion = TestDetektion()
        assertThat(TxtOutputReport().render(detektion))
            .isEqualTo("")
    }

    @Test
    fun `renders one`() {
        val detektion = TestDetektion(createIssue())
        assertThat(TxtOutputReport().render(detektion))
            .isEqualTo("TestSmell - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature\n")
    }

    @Test
    fun `renders multiple`() {
        val detektion = TestDetektion(
            createIssue(ruleName = "TestSmellA"),
            createIssue(ruleName = "TestSmellB"),
            createIssue(ruleName = "TestSmellC"),
        )
        assertThat(TxtOutputReport().render(detektion))
            .isEqualTo(
                """
                    TestSmellA - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature
                    TestSmellB - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature
                    TestSmellC - [TestEntity] at TestFile.kt:1:1 - Signature=TestEntitySignature

                """.trimIndent()
            )
    }
}
