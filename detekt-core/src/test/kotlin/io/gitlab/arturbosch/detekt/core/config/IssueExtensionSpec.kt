package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.reporting.filterAutoCorrectedIssues
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createCorrectableFinding
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IssueExtensionSpec {

    private val issues = mapOf(
        "Ruleset1" to listOf(createFinding(), createCorrectableFinding()),
        "Ruleset2" to listOf(createFinding())
    )

    @Test
    fun `compute weighted amount of issues`() {
        val detektion = object : TestDetektion() {
            override val findings: Map<String, List<Finding>> = issues
        }

        val amount = detektion.getOrComputeWeightedAmountOfIssues(Config.empty)
        assertThat(amount).isEqualTo(3)
    }

    @Nested
    inner class `filter auto corrected issues` {

        @Test
        fun `excludeCorrectable = false (default)`() {
            val detektion = TestDetektion(createFinding(), createCorrectableFinding())
            val findings = detektion.filterAutoCorrectedIssues(Config.empty)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `excludeCorrectable = true`() {
            val config = TestConfig("maxIssues" to "0", "excludeCorrectable" to "true")
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = issues
            }

            val findings = detektion.filterAutoCorrectedIssues(config)
            assertThat(findings).hasSize(2)
        }
    }
}
