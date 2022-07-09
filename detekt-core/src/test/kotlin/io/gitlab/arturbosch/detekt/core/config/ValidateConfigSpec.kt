package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.CommaSeparatedPattern
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.yamlConfig
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ValidateConfigSpec {

    private val baseline = yamlConfig("config_validation/baseline.yml")

    @Test
    fun `passes for same config test`() {
        val result = validateConfig(baseline, baseline)
        assertThat(result).isEmpty()
    }

    @Test
    fun `passes for properties which may appear on rules and rule sets but may be not present in default config`() {
        val result = validateConfig(
            yamlConfig("config_validation/default-excluded-properties.yml"),
            baseline
        )
        assertThat(result).isEmpty()
    }

    @Test
    fun `reports different rule set name`() {
        val result = validateConfig(
            yamlConfig("config_validation/other-ruleset-name.yml"),
            baseline
        )
        assertThat(result).contains(propertyDoesNotExists("code-smell"))
    }

    @Test
    fun `reports different nested property names`() {
        val result = validateConfig(
            yamlConfig("config_validation/other-nested-property-names.yml"),
            baseline
        )
        assertThat(result).contains(
            propertyDoesNotExists("complexity>LongLongMethod"),
            propertyDoesNotExists("complexity>LongParameterList>enabled"),
            propertyDoesNotExists("complexity>LargeClass>howMany"),
            propertyDoesNotExists("complexity>InnerMap>InnerKey"),
            propertyDoesNotExists("complexity>InnerMap>Inner2>nestedActive")
        )
    }

    @Test
    fun `reports nested configuration expected`() {
        val result = validateConfig(
            yamlConfig("config_validation/no-nested-config.yml"),
            baseline
        )
        assertThat(result).contains(
            nestedConfigurationExpected("complexity"),
            nestedConfigurationExpected("style>WildcardImport")
        )
    }

    @Test
    fun `reports unexpected nested configs`() {
        // note that the baseline config is now the first argument
        val result = validateConfig(baseline, yamlConfig("config_validation/no-value.yml"))
        assertThat(result).contains(
            unexpectedNestedConfiguration("style"),
            unexpectedNestedConfiguration("comments")
        )
    }

    @Test
    fun `returns an error for an invalid config type`() {
        val invalidConfig = TestConfig()
        assertThatIllegalStateException().isThrownBy {
            validateConfig(invalidConfig, baseline)
        }.withMessageStartingWith("Unsupported config type for validation")
    }

    @Test
    fun `returns an error for an invalid baseline`() {
        val invalidBaseline = TestConfig()
        assertThatIllegalArgumentException().isThrownBy {
            validateConfig(Config.empty, invalidBaseline)
        }.withMessageStartingWith("Only supported baseline config is the YamlConfig.")
    }

    @Test
    fun `returns an error for an empty baseline`() {
        val invalidBaseline = Config.empty
        assertThatIllegalArgumentException().isThrownBy {
            validateConfig(Config.empty, invalidBaseline)
        }.withMessageStartingWith("Cannot validate configuration based on an empty baseline config.")
    }

    @Nested
    inner class `validate composite configurations` {

        @Test
        fun `passes for same left, right and baseline config`() {
            val result = validateConfig(CompositeConfig(baseline, baseline), baseline)
            assertThat(result).isEmpty()
        }

        @Test
        fun `passes for empty configs`() {
            val result = validateConfig(CompositeConfig(Config.empty, Config.empty), baseline)
            assertThat(result).isEmpty()
        }

        @Test
        fun `finds accumulated errors`() {
            val result = validateConfig(
                CompositeConfig(
                    yamlConfig("config_validation/other-nested-property-names.yml"),
                    yamlConfig("config_validation/no-nested-config.yml")
                ),
                baseline
            )

            assertThat(result).contains(
                nestedConfigurationExpected("complexity"),
                nestedConfigurationExpected("style>WildcardImport"),
                propertyDoesNotExists("complexity>LongLongMethod"),
                propertyDoesNotExists("complexity>LongParameterList>enabled"),
                propertyDoesNotExists("complexity>LargeClass>howMany"),
                propertyDoesNotExists("complexity>InnerMap>InnerKey"),
                propertyDoesNotExists("complexity>InnerMap>Inner2>nestedActive")
            )
        }
    }

    @Nested
    inner class `configure additional exclude paths` {

        private fun patterns(str: String) = CommaSeparatedPattern(str).mapToRegex()

        @Test
        fun `does not report any complexity properties`() {
            val result = validateConfig(
                yamlConfig("config_validation/other-nested-property-names.yml"),
                baseline,
                patterns("complexity")
            )
            assertThat(result).isEmpty()
        }

        @Test
        fun `does not report 'complexity_LargeClass_howMany'`() {
            val result = validateConfig(
                yamlConfig("config_validation/other-nested-property-names.yml"),
                baseline,
                patterns(".*>.*>howMany")
            )

            assertThat(result).contains(
                propertyDoesNotExists("complexity>LongLongMethod"),
                propertyDoesNotExists("complexity>LongParameterList>enabled"),
                propertyDoesNotExists("complexity>InnerMap>InnerKey"),
                propertyDoesNotExists("complexity>InnerMap>Inner2>nestedActive")
            )

            assertThat(result).doesNotContain(
                propertyDoesNotExists("complexity>LargeClass>howMany")
            )
        }

        @Test
        @DisplayName("does not report .*>InnerMap")
        fun `does not report innerMap`() {
            val result = validateConfig(
                yamlConfig("config_validation/other-nested-property-names.yml"),
                baseline,
                patterns(".*>InnerMap")
            )

            assertThat(result).contains(
                propertyDoesNotExists("complexity>LargeClass>howMany"),
                propertyDoesNotExists("complexity>LongLongMethod"),
                propertyDoesNotExists("complexity>LongParameterList>enabled")
            )

            assertThat(result).doesNotContain(
                propertyDoesNotExists("complexity>InnerMap>InnerKey"),
                propertyDoesNotExists("complexity>InnerMap>Inner2>nestedActive")
            )
        }
    }

    @Nested
    inner class `deprecated configuration option` {

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun `reports a deprecated property as a warning`(warningsAsErrors: Boolean) {
            val config = yamlConfigFromContent(
                """
                    config:
                      warningsAsErrors: $warningsAsErrors
                    naming:
                      FunctionParameterNaming:
                        ignoreOverriddenFunctions: ''
                """.trimIndent()
            )

            val result = validateConfig(config, config)

            assertThat(result).contains(
                propertyIsDeprecated(
                    "naming>FunctionParameterNaming>ignoreOverriddenFunctions",
                    "Use `ignoreOverridden` instead",
                    reportAsError = warningsAsErrors
                )
            )
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `reports a string that should be an array as a warning`(warningsAsErrors: Boolean) {
        val config = yamlConfigFromContent(
            """
                config:
                  warningsAsErrors: $warningsAsErrors
                style:
                  MagicNumber:
                    ignoreNumbers: '-1,0,1,2'
            """.trimIndent()
        )

        val result = validateConfig(config, baseline)

        assertThat(result).contains(
            propertyShouldBeAnArray(
                "style>MagicNumber>ignoreNumbers",
                reportAsError = warningsAsErrors
            )
        )
    }
}
