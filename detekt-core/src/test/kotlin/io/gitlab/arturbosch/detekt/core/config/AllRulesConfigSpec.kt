package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.yamlConfig
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AllRulesConfigSpec {
    private val emptyYamlConfig = yamlConfigFromContent("")

    @Nested
    inner class ParentPath {
        private val rulesetId = "style"
        private val rulesetConfig = yamlConfig("/configs/single-rule-in-style-ruleset.yml").subConfig(rulesetId)

        @Test
        fun `is derived from the original config`() {
            val subject = AllRulesConfig(
                originalConfig = rulesetConfig,
                defaultConfig = emptyYamlConfig,
            )
            val actual = subject.parentPath
            assertThat(actual).isEqualTo(rulesetId)
        }

        @Test
        fun `is derived from the default config if unavailable in original config`() {
            val subject = AllRulesConfig(
                originalConfig = emptyYamlConfig,
                defaultConfig = rulesetConfig,
            )
            val actual = subject.parentPath
            assertThat(actual).isEqualTo(rulesetId)
        }
    }

    @Nested
    inner class DeactivateDeprecatedRule {

        @Test
        fun `rule is active if not deprecated`() {
            val subject = AllRulesConfig(
                originalConfig = emptyYamlConfig,
                defaultConfig = emptyYamlConfig,
                deprecatedRuleIds = emptySet()
            )
                .subConfig("ruleset")
                .subConfig("ARule")

            val actual = subject.valueOrDefault(Config.ACTIVE_KEY, false)

            assertThat(actual).isTrue
        }

        @Test
        fun `rule is inactive if deprecated`() {
            val subject = AllRulesConfig(
                originalConfig = emptyYamlConfig,
                defaultConfig = emptyYamlConfig,
                deprecatedRuleIds = setOf("ruleset > ARule")
            )
                .subConfig("ruleset")
                .subConfig("ARule")

            val actual = subject.valueOrDefault(Config.ACTIVE_KEY, false)

            assertThat(actual).isFalse
        }
    }
}
