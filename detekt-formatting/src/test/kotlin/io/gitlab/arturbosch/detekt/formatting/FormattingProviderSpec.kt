package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FormattingProviderSpec {
    @Test
    fun `preferred ktlint rule ordering is applied`() {
        val subject: RuleSet = FormattingProvider().instance(Config.empty)

        val indexOfFirstLateRule = subject.rules.indexOfFirst { (it as FormattingRule).runAsLateAsPossible }
        val indexOfLastRegularRule = subject.rules.indexOfLast { (it as FormattingRule).runAsLateAsPossible.not() }

        assertThat(indexOfFirstLateRule).isGreaterThan(indexOfLastRegularRule)
    }
}
