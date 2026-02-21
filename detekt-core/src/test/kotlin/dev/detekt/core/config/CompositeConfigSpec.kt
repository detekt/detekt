package dev.detekt.core.config

import dev.detekt.core.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CompositeConfigSpec {

    private val overrideConfig = yamlConfigFromContent(
        """
            style:
              WildcardImport:
                active: false
              NoElseInWhenExpression:
                active: false
              MagicNumber:
                ignoreHashCodeFunction: true
                ignorePropertyDeclaration: true
                ignoreAnnotation: true
                ignoreNumbers: ['-1', '0', '1', '2', '100', '1000']
              LargeClass:
                active: truuu
        """.trimIndent()
    )
    private val defaultConfig = yamlConfigFromContent(
        """
            code-smell:
              LongMethod:
                active: true
                allowedLines: 20
              LongParameterList:
                active: false
                threshold: 5
              LargeClass:
                active: false
                threshold: 70
              InnerMap:
                Inner1:
                  active: true
                Inner2:
                  active: true
            
            style:
              WildcardImport:
                active: true
              NoElseInWhenExpression:
                active: true
              MagicNumber:
                active: true
                ignoreNumbers: ['-1', '0', '1', '2']
        """.trimIndent()
    )
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

        assertThatThrownBy { config.valueOrDefault("active", true) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("The string doesn't represent a boolean value: truuu")
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
