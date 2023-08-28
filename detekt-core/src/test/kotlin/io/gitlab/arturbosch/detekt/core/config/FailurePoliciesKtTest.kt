package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.api.IssuesFound
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createCorrectableFinding
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FailurePoliciesKtTest {
    @Nested
    inner class `None Allowed` {
        private val subject = RulesSpec.FailurePolicy.NoneAllowed
        private val correctableFinding: Finding = createCorrectableFinding()

        @Test
        fun `does not fail without a finding`() {
            val result = TestDetektion()
            subject.check(result, Config.empty)
        }

        @Test
        fun `fails on at least one finding`() {
            val config = TestConfig("excludeCorrectable" to "false")
            val result = TestDetektion(correctableFinding)
            assertThatThrownBy { subject.check(result, config) }
                .isInstanceOf(IssuesFound::class.java)
        }

        @Test
        fun `does not fail on correctable finding if configured`() {
            val config = TestConfig("excludeCorrectable" to "true")
            val result = TestDetektion(correctableFinding)

            subject.check(result, config)
        }
    }
}
