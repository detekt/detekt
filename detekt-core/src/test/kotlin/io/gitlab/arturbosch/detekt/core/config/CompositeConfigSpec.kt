package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CompositeConfigSpec {

    @Nested
    inner class `both configs should be considered` {

        private val second = yamlConfig("composite-test.yml")
        private val first = yamlConfig("detekt.yml")
        private val compositeConfig = CompositeConfig(second, first)

        @Test
        fun `should have style sub config with active false which is overridden in "second" config regardless of default value`() {
            val styleConfig = compositeConfig.subConfig("style").subConfig("WildcardImport")
            assertThat(styleConfig.valueOrDefault("active", true)).isEqualTo(false)
            assertThat(styleConfig.valueOrDefault("active", false)).isEqualTo(false)
        }

        @Test
        fun `should have code smell sub config with LongMethod threshold 20 from _first_ config`() {
            val codeSmellConfig = compositeConfig.subConfig("code-smell").subConfig("LongMethod")
            assertThat(codeSmellConfig.valueOrDefault("threshold", -1)).isEqualTo(20)
        }

        @Test
        fun `should use the default as both part configurations do not have the value`() {
            assertThat(compositeConfig.valueOrDefault("TEST", 42)).isEqualTo(42)
        }

        @Test
        fun `should return a string based on default value`() {
            val config = compositeConfig.subConfig("style").subConfig("MagicNumber")
            val value = config.valueOrDefault("ignoreNumbers", emptyList<String>())
            assertThat(value).isEqualTo(listOf("-1", "0", "1", "2", "100", "1000"))
        }

        @Test
        fun `should fail with a meaningful exception when boolean property is invalid`() {
            val config = compositeConfig.subConfig("style").subConfig("LargeClass")

            val expectedErrorMessage = "Value \"truuu\" set for config parameter \"style > LargeClass > active\" " +
                "is not of required type Boolean"

            assertThatThrownBy {
                config.valueOrDefault("active", true)
            }.isInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining(expectedErrorMessage)
        }
    }
}
