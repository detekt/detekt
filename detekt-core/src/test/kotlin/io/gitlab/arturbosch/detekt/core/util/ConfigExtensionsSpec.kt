package io.gitlab.arturbosch.detekt.core.util

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

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

@Nested
class ShouldAnalyzeFileSpec {

    private val basePath = Path("/cases").absolute()
    private val file = compileContentForTest("", path = Path("/cases/Default.kt"))

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
