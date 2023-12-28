package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RuleSetSpec {

    @Nested
    inner class `Config isActive` {

        @Test
        fun `use the provided value when defined`() {
            val config = yamlConfig("configs/deactivated_ruleset.yml")
            assertThat(config.subConfig("comments").isActive(true)).isFalse()
            assertThat(config.subConfig("comments").isActive(false)).isFalse()
        }

        @Test
        fun `use the default value when it is not defined`() {
            assertThat(Config.empty.isActive(true)).isTrue()
            assertThat(Config.empty.isActive(false)).isFalse()
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
