package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.reporting.filterAutoCorrectedIssues
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createCorrectableFinding
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class IssueExtensionSpec : Spek({

    val issues by memoized {
        mapOf(
            Pair("Ruleset1", listOf(createFinding(), createCorrectableFinding())),
            Pair("Ruleset2", listOf(createFinding()))
        )
    }

    test("compute weighted amount of issues") {
        val detektion = object : TestDetektion() {
            override val findings: Map<String, List<Finding>> = issues
        }

        val amount = detektion.getOrComputeWeightedAmountOfIssues(Config.empty)
        assertThat(amount).isEqualTo(3)
    }

    describe("filter auto corrected issues") {

        it("excludeCorrectable = false (default)") {
            val detektion = TestDetektion(createFinding(), createCorrectableFinding())
            val findings = detektion.filterAutoCorrectedIssues(Config.empty)
            assertThat(findings).hasSize(1)
        }

        it("excludeCorrectable = true") {
            val config = TestConfig(mapOf("maxIssues" to "0", "excludeCorrectable" to "true"))
            val detektion = object : TestDetektion() {
                override val findings: Map<String, List<Finding>> = issues
            }

            val findings = detektion.filterAutoCorrectedIssues(config)
            assertThat(findings).hasSize(2)
        }
    }
})
