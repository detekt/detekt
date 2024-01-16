package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.api.IssuesFound
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE
import org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE

class FailurePoliciesKtTest {

    @Nested
    inner class `Never Fail` {
        val subject = RulesSpec.FailurePolicy.NeverFail

        @Test
        fun `does not fail without a finding`() {
            val result = TestDetektion()

            subject.check(result, Config.empty)
        }

        @ParameterizedTest
        @EnumSource(value = Severity::class)
        fun `does not fail on finding with any severity`(issueSeverity: Severity) {
            val result = TestDetektion(createFinding(severity = issueSeverity))

            subject.check(result, Config.empty)
        }
    }

    @Nested
    inner class `Fail On Severity` {
        val subject = RulesSpec.FailurePolicy.FailOnSeverity(Severity.Warning)

        @Test
        fun `does not fail without a finding`() {
            val result = TestDetektion()

            subject.check(result, Config.empty)
        }

        @ParameterizedTest
        @EnumSource(value = Severity::class, names = ["Info"], mode = EXCLUDE)
        fun `fails on at least one finding at or above threshold`(issueSeverity: Severity) {
            val result = TestDetektion(createFinding(severity = issueSeverity))

            assertThatThrownBy { subject.check(result, Config.empty) }
                .isInstanceOf(IssuesFound::class.java)
        }

        @ParameterizedTest
        @EnumSource(value = Severity::class, names = ["Info"], mode = INCLUDE)
        fun `does not fail on finding below threshold`(issueSeverity: Severity) {
            val result = TestDetektion(createFinding(severity = issueSeverity))

            subject.check(result, Config.empty)
        }

        @Test
        fun `does not fail on correctable finding if configured`() {
            val result = TestDetektion(createFinding(severity = Severity.Error, autoCorrectEnabled = true))
            val config = TestConfig("excludeCorrectable" to "true")

            subject.check(result, config)
        }
    }
}
