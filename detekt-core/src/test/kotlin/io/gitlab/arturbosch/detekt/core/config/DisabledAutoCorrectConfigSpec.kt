package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DisabledAutoCorrectConfigSpec {
    private val rulesetId = "style"
    private val rulesetConfig = yamlConfig("/configs/single-rule-in-style-ruleset.yml").subConfig(rulesetId)

    @Test
    fun `parent path is derived from wrapped config`() {
        val subject = DisabledAutoCorrectConfig(rulesetConfig)
        val actual = subject.parentPath
        assertThat(actual).isEqualTo(rulesetId)
    }
}
