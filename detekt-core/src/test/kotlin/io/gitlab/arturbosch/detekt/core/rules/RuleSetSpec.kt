package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class RuleSetSpec {

    @Nested
    inner class `should rule analyze a file` {

        private val file = compileContentForTest("", basePath = Path("/cases"), path = Path("/cases/Default.kt"))

        @Test
        fun `analyzes file with an empty config`() {
            val config = Config.empty
            assertThat(config.shouldAnalyzeFile(file)).isTrue()
        }

        @Test
        @DisplayName("should not analyze file with **/*.kt excludes")
        fun ignoreExcludedKt() {
            val config = TestConfig(Config.EXCLUDES_KEY to listOf("**/*.kt"))
            assertThat(config.shouldAnalyzeFile(file)).isFalse()
        }

        @Test
        fun `Only check relative path`() {
            val config = TestConfig(Config.EXCLUDES_KEY to listOf("**/cases/*.kt"))
            assertThat(config.shouldAnalyzeFile(file)).isTrue()
        }

        @Test
        fun `should not analyze file as its path is both included and excluded`() {
            val config = TestConfig(
                Config.EXCLUDES_KEY to listOf("**/*.kt"),
                Config.INCLUDES_KEY to listOf("**/*.kt"),
            )
            assertThat(config.shouldAnalyzeFile(file)).isFalse()
        }
    }
}
