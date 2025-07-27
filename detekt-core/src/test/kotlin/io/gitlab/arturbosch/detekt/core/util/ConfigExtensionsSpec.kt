package io.gitlab.arturbosch.detekt.core.util

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class IsActiveOrDefaultSpec {

    @Test
    fun `use the provided value when defined`() {
        val config = yamlConfigFromContent(
            """
                comments:
                  active: false
            """.trimIndent()
        )
        assertThat(config.subConfig("comments").isActiveOrDefault(true)).isFalse()
        assertThat(config.subConfig("comments").isActiveOrDefault(false)).isFalse()
    }

    @Test
    fun `use the default value when it is not defined`() {
        assertThat(Config.empty.isActiveOrDefault(true)).isTrue()
        assertThat(Config.empty.isActiveOrDefault(false)).isFalse()
    }
}

class ShouldAnalyzeFileSpec {

    private val basePath = resourceAsPath("cases")
    private val file = compileForTest(basePath.resolve("Default.kt"))

    @Test
    fun `analyzes file with an empty config`() {
        val config = Config.empty
        assertThat(config.shouldAnalyzeFile(file, basePath)).isTrue()
    }

    @Test
    @DisplayName("should not analyze file with **/*.kt excludes")
    fun ignoreExcludedKt() {
        val config = TestConfig(Config.EXCLUDES_KEY to listOf("**/*.kt"))
        assertThat(config.shouldAnalyzeFile(file, basePath)).isFalse()
    }

    @Test
    fun `Only check relative path`() {
        val config = TestConfig(Config.EXCLUDES_KEY to listOf("**/cases/*.kt"))
        assertThat(config.shouldAnalyzeFile(file, basePath)).isTrue()
    }

    @Test
    fun `should not analyze file as its path is both included and excluded`() {
        val config = TestConfig(
            Config.EXCLUDES_KEY to listOf("**/*.kt"),
            Config.INCLUDES_KEY to listOf("**/*.kt"),
        )
        assertThat(config.shouldAnalyzeFile(file, basePath)).isFalse()
    }
}
