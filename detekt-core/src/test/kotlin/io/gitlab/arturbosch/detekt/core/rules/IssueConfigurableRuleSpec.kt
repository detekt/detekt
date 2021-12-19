package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.IssueConfigurableRule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class IssueConfigurableRuleSpec : Spek({

    val id by memoized { "TestRule" }
    val description by memoized { "A Test Rule" }
    val debt by memoized { "debt" }

    fun createRule(config: TestConfig) = object : IssueConfigurableRule(config) {
        override val ruleId = id
        override val severity = Severity.Defect
        override val description = description
        override val defaultDebt = Debt.FIVE_MINS
    }

    describe("IssueConfigurableRule") {
        it("should produce an issue that uses a default debt when no configuration is found") {
            val config = TestConfig()
            val testRule = createRule(config)
            assert(testRule.issue.debt == Debt.FIVE_MINS)
        }
        it("should produce an issue with a configured debt of 20 minutes") {
            val config = TestConfig(mapOf(debt to 20))
            val testRule = createRule(config)
            assert(testRule.issue.debt == Debt.TWENTY_MINS)
        }
    }
})
