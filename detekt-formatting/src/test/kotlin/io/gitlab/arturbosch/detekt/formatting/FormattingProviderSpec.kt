package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FormattingProviderSpec {
    @Test
    fun `preferred ktlint rule ordering is applied`() {
        val subject: RuleSet = FormattingProvider().instance(Config.empty)

        val formattingRules = subject.rules.map { it as FormattingRule }
        val indexOfFirstLateRule = formattingRules.indexOfFirst { it.runAsLateAsPossible }
        val indexOfLastRegularRule = formattingRules.indexOfLast { it.runAsLateAsPossible.not() }

        assertThat(indexOfFirstLateRule).isGreaterThan(indexOfLastRegularRule)
    }
}
