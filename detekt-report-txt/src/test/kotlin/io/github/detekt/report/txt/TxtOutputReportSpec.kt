package io.github.detekt.report.txt

import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInstance
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
        val location = createLocation()
        val detektion = TestDetektion(createIssue(createRuleInstance(), location))
        assertThat(TxtOutputReport().render(detektion))
            .isEqualTo("TestSmell - [TestEntity] at ${location.compact()} - Signature=TestEntitySignature\n")
    }

    @Test
    fun `renders multiple`() {
        val location = createLocation()
        val detektion = TestDetektion(
            createIssue(createRuleInstance("TestSmellA"), location),
            createIssue(createRuleInstance("TestSmellB"), location),
            createIssue(createRuleInstance("TestSmellC"), location),
        )
        assertThat(TxtOutputReport().render(detektion))
            .isEqualTo(
                """
                    TestSmellA - [TestEntity] at ${location.compact()} - Signature=TestEntitySignature
                    TestSmellB - [TestEntity] at ${location.compact()} - Signature=TestEntitySignature
                    TestSmellC - [TestEntity] at ${location.compact()} - Signature=TestEntitySignature

                """.trimIndent()
            )
    }
}
