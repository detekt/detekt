package dev.detekt.core.config

import dev.detekt.api.Config
import dev.detekt.core.config.validation.DeprecatedRule
import dev.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AllRulesConfigSpec {
    private val emptyYamlConfig = yamlConfigFromContent("")

    @Test
    fun verifyValue() {
        val subject = AllRulesConfig(
            wrapped = yamlConfigFromContent(
                """
                    style:
                      MaxLineLength:
                        maxLineLength: 100
                """.trimIndent()
            ),
            deprecatedRules = emptySet(),
        )

        val subConfig = subject.subConfig("style")
            .subConfig("MaxLineLength")
        assertThat(subConfig.valueOrDefault("maxLineLength", 0)).isEqualTo(100)
        assertThat(subConfig.valueOrNull<Int>("maxLineLength")).isEqualTo(100)
    }

    @Nested
    inner class ParentPath {
        private val rulesetId = "style"
        private val rulesetConfig = yamlConfigFromContent(
            """
                style:
                  MaxLineLength:
                    maxLineLength: 100
            """.trimIndent()
        ).subConfig(rulesetId)

        @Test
        fun `is derived from the original config`() {
            val subject = AllRulesConfig(
                wrapped = rulesetConfig,
                deprecatedRules = emptySet(),
            )
            val actual = subject.key
            assertThat(actual).isEqualTo(rulesetId)
        }

        @Test
        fun `is derived from the default config if unavailable in original config`() {
            val subject = AllRulesConfig(
                wrapped = emptyYamlConfig,
                deprecatedRules = emptySet(),
            )
            val actual = subject.key
            assertThat(actual).isEqualTo(null)
        }
    }

    @Nested
    inner class Parent {
        private val rulesetConfig = yamlConfigFromContent(
            """
                style:
                  MaxLineLength:
                    maxLineLength: 100
            """.trimIndent()
        )

        @Test
        fun `is the parent`() {
            val subject = AllRulesConfig(
                wrapped = rulesetConfig,
                deprecatedRules = emptySet(),
            )
            val actual = subject.subConfig("style").parent
            assertThat(actual).isEqualTo(subject)
        }

        @Test
        fun `is the parent for all subConfig`() {
            val subject = AllRulesConfig(
                wrapped = rulesetConfig,
                deprecatedRules = emptySet(),
            )

            assertThat(subject.subConfigKeys()).contains("style")
        }
    }

    @Nested
    inner class DeactivateDeprecatedRule {

        @Test
        fun `rule is active if not deprecated`() {
            val subject = AllRulesConfig(
                wrapped = emptyYamlConfig,
                deprecatedRules = emptySet()
            )
                .subConfig("ruleset")
                .subConfig("ARule")

            val actual = subject.valueOrDefault(Config.ACTIVE_KEY, false)

            assertThat(actual).isTrue
        }

        @Test
        fun `rule is inactive if deprecated`() {
            val subject = AllRulesConfig(
                wrapped = emptyYamlConfig,
                deprecatedRules = setOf(DeprecatedRule("ruleset", "ARule", "")),
            )
                .subConfig("ruleset")
                .subConfig("ARule")

            val actual = subject.valueOrDefault(Config.ACTIVE_KEY, false)

            assertThat(actual).isFalse
        }
    }
}
