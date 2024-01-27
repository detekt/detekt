package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DisabledAutoCorrectConfigSpec {
    private val rulesetId = "style"
    private val configSingleRuleInStyle = yamlConfigFromContent(
        """
            style:
              MaxLineLength:
                maxLineLength: 100
        """.trimIndent()
    )

    @Test
    fun `subConfigs returns the expected number of sub configs`() {
        val subject = DisabledAutoCorrectConfig(configSingleRuleInStyle)
        val actual = subject.subConfigKeys()
        assertThat(actual).containsExactly("style")
    }

    @Test
    fun `parent path is derived from wrapped config`() {
        val subject = DisabledAutoCorrectConfig(configSingleRuleInStyle.subConfig(rulesetId))
        val actual = subject.parentPath
        assertThat(actual).isEqualTo(rulesetId)
    }

    @Test
    fun `verify the autocorrect field false in case the autoCorrect not present into yaml config`() {
        val config = yamlConfigFromContent(
            """
                comments:
                  ClassDoc:
                    test: true
            """.trimMargin()
        )

        val commentsConfig = DisabledAutoCorrectConfig(config.subConfig("comments"))
        commentsConfig.subConfig("ClassDoc").let { classDocConfig ->
            assertThat(classDocConfig.valueOrDefault("autoCorrect", true)).isFalse()
            assertThat(classDocConfig.valueOrNull<Boolean>("autoCorrect")).isFalse()
        }
    }

    @Test
    fun `verify the disable auto correct config return fields as normal`() {
        val config = yamlConfigFromContent(
            """
                comments:
                  ClassDoc:
                    test: true
                  FunctionDoc:
                    test: true
            """.trimMargin()
        )

        val commentsConfig = DisabledAutoCorrectConfig(config.subConfig("comments"))

        commentsConfig.subConfig("ClassDoc").let { classDocConfig ->
            assertThat(classDocConfig.valueOrDefault("test", true)).isTrue()
            assertThat(classDocConfig.valueOrNull<Boolean>("test")).isTrue()
        }

        commentsConfig.subConfig("FunctionDoc").let { functionDocConfig ->
            assertThat(functionDocConfig.valueOrDefault("test", true)).isTrue()
            assertThat(functionDocConfig.valueOrNull<Boolean>("test")).isTrue()
        }
    }

    @Test
    fun `verify the autocorrect field always false`() {
        val config = yamlConfigFromContent(
            """
                comments:
                  ClassDoc:
                    autoCorrect: true
                  FunctionDoc:
                    autoCorrect: false
            """.trimIndent()
        )

        val commentsConfig = DisabledAutoCorrectConfig(config.subConfig("comments"))

        commentsConfig.subConfig("ClassDoc").let { classDocConfig ->
            assertThat(classDocConfig.valueOrDefault("autoCorrect", true)).isFalse()
            assertThat(classDocConfig.valueOrNull<Boolean>("autoCorrect")).isFalse()
        }

        commentsConfig.subConfig("FunctionDoc").let { functionDocConfig ->
            assertThat(functionDocConfig.valueOrDefault("autoCorrect", true)).isFalse()
            assertThat(functionDocConfig.valueOrNull<Boolean>("autoCorrect")).isFalse()
        }
    }

    @Test
    fun `parent returns the parent instance`() {
        val subject = DisabledAutoCorrectConfig(configSingleRuleInStyle)
        val actual = subject.subConfig(rulesetId).parent
        assertThat(actual).isEqualTo(subject)
    }
}
