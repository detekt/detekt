package dev.detekt.core.config

import dev.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CompositeConfigSpec {

    private val overrideConfig = yamlConfig("composite-test.yml")
    private val defaultConfig = yamlConfig("detekt.yml")
    private val compositeConfig = CompositeConfig(
        lookFirst = overrideConfig,
        lookSecond = defaultConfig
    )

    @Test
    fun `should return a list of sub configs of each config`() {
        val subConfigs = compositeConfig.subConfigKeys()
        assertThat(subConfigs).containsExactly("style", "code-smell")
    }

    @Test
    fun `should return a list of sub configs of each config with parent path`() {
        val subConfigs = compositeConfig.subConfigKeys()
        assertThat(subConfigs).containsExactly("style", "code-smell")
    }

    @Test
    fun `should have style sub config with active false which is overridden in second config regardless of default value`() {
        val styleConfig = compositeConfig.subConfig("style").subConfig("WildcardImport")
        assertThat(styleConfig.valueOrDefault("active", true)).isEqualTo(false)
        assertThat(styleConfig.valueOrDefault("active", false)).isEqualTo(false)
    }

    @Test
    fun `should have code smell sub config with LongMethod allowedLines 20 from _default_ config`() {
        val codeSmellConfig = compositeConfig.subConfig("code-smell").subConfig("LongMethod")
        assertThat(codeSmellConfig.valueOrDefault("allowedLines", -1)).isEqualTo(20)
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

    @Nested
    inner class ParentPath {

        @Test
        fun `is derived from the _override_ config if available`() {
            val subject = compositeConfig.subConfig("style")
            val actual = subject.parentPath
            assertThat(actual).isEqualTo("style")
        }

        @Test
        fun `is derived from the default config if unavailable in original config`() {
            val subject = compositeConfig.subConfig("code-smell")
            val actual = subject.parentPath
            assertThat(actual).isEqualTo("code-smell")
        }
    }

    @Test
    fun `parent returns the parent instance`() {
        val subject = compositeConfig
        val actual = subject.subConfig("style").parent
        assertThat(actual).isEqualTo(subject)
    }
}
