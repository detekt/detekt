package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RuleSetSpec {

    @Nested
    inner class `should rule set be used` {

        @Test
        fun `is explicitly deactivated`() {
            val config = yamlConfigFromContent(
                """
                    comments:
                      active: false
                """.trimIndent()
            )
            assertThat(config.subConfig("comments").isActive()).isFalse()
        }

        @Test
        fun `is active with an empty config`() {
            assertThat(Config.empty.isActive()).isTrue()
        }
    }

    @Nested
    inner class `should rule analyze a file` {

        private val file = compileForTest(resourceAsPath("/cases/Default.kt"))

        @Test
        fun `analyzes file with an empty config`() {
            val config = Config.empty
            assertThat(config.subConfig("comments").shouldAnalyzeFile(file)).isTrue()
        }

        @Test
        @DisplayName("should not analyze file with **/*.kt excludes")
        fun ignoreExcludedKt() {
            val config = TestConfig(Config.EXCLUDES_KEY to listOf("**/*.kt"))
            assertThat(config.subConfig("comments").shouldAnalyzeFile(file)).isFalse()
        }

        @Test
        fun `should not analyze file as its path is both included and excluded`() {
            val config = TestConfig(
                Config.EXCLUDES_KEY to listOf("**/*.kt"),
                Config.INCLUDES_KEY to listOf("**/*.kt"),
            )
            assertThat(config.subConfig("comments").shouldAnalyzeFile(file)).isFalse()
        }
    }
}
