package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AllRulesConfigSpec {
    @Nested
    inner class ParentPath {
        private val rulesetId = "style"
        private val rulesetConfig = yamlConfig("/configs/single-rule-in-style-ruleset.yml").subConfig(rulesetId)
        private val emptyConfig = Config.empty

        @Test
        fun `is derived from the original config`() {
            val subject = AllRulesConfig(
                originalConfig = rulesetConfig,
                defaultConfig = emptyConfig,
            )
            val actual = subject.parentPath
            assertThat(actual).isEqualTo(rulesetId)
        }

        @Test
        fun `is derived from the default config if unavailable in original config`() {
            val subject = AllRulesConfig(
                originalConfig = emptyConfig,
                defaultConfig = rulesetConfig,
            )
            val actual = subject.parentPath
            assertThat(actual).isEqualTo(rulesetId)
        }
    }
}
