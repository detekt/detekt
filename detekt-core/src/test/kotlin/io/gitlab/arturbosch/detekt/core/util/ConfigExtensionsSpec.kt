package io.gitlab.arturbosch.detekt.core.util

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConfigExtensionsSpec {

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
