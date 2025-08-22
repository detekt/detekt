package dev.detekt.core.config

import dev.detekt.test.yamlConfigFromContent
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

    private val configWithAutoCorrect = yamlConfigFromContent(
        """
            style:
              autoCorrect: true
              MagicNumber:
                autoCorrect: true
              MagicString:
                autoCorrect: false
            
            comments:
              autoCorrect: false
              ClassDoc:
                autoCorrect: true
              FunctionDoc:
                autoCorrect: false
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
              autoCorrect: false
              ClassDoc:
                test: true
            """.trimMargin()
        )

        val commentsConfig = DisabledAutoCorrectConfig(config.subConfig("comments"))
        commentsConfig.subConfig("ClassDoc").let { classDocConfig ->
            assertThat(classDocConfig.valueOrDefault("autoCorrect", true)).isFalse()
            assert(classDocConfig.valueOrNull<Boolean>("autoCorrect") == false)
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
            assert(classDocConfig.valueOrNull<Boolean>("test") == true)
        }

        commentsConfig.subConfig("FunctionDoc").let { functionDocConfig ->
            assertThat(functionDocConfig.valueOrDefault("test", true)).isTrue()
            assert(functionDocConfig.valueOrNull<Boolean>("test") == true)
        }
    }

    @Test
    fun `verify the autocorrect field always false`() {
        val commentsConfig = DisabledAutoCorrectConfig(configWithAutoCorrect.subConfig("comments"))

        commentsConfig.subConfig("ClassDoc").let { classDocConfig ->
            assertThat(classDocConfig.valueOrDefault("autoCorrect", true)).isFalse()
            assert(classDocConfig.valueOrNull<Boolean>("autoCorrect") == false)
        }

        commentsConfig.subConfig("FunctionDoc").let { functionDocConfig ->
            assertThat(functionDocConfig.valueOrDefault("autoCorrect", true)).isFalse()
            assert(functionDocConfig.valueOrNull<Boolean>("autoCorrect") == false)
        }
    }

    @Test
    fun `parent returns the parent instance`() {
        val subject = DisabledAutoCorrectConfig(configSingleRuleInStyle)
        val actual = subject.subConfig(rulesetId).parent
        assertThat(actual).isEqualTo(subject)
    }
}
