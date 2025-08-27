package dev.detekt.core.config.validation

import dev.detekt.api.Config
import dev.detekt.api.Notification
import dev.detekt.core.config.CompositeConfig
import dev.detekt.core.config.YamlConfig
import dev.detekt.core.config.validation.InvalidPropertiesConfigValidator.Companion.nestedConfigurationExpected
import dev.detekt.core.config.validation.InvalidPropertiesConfigValidator.Companion.propertyDoesNotExists
import dev.detekt.core.config.validation.InvalidPropertiesConfigValidator.Companion.unexpectedNestedConfiguration
import dev.detekt.test.yamlConfig
import dev.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class InvalidPropertiesConfigValidatorSpec {
    private val deprecatedProperties = setOf(
        DeprecatedProperty(
            ruleSetId = "complexity",
            ruleName = "LongParameterList",
            propertyName = "threshold",
            description = "use xxx instead"
        )
    )
    private val baseline = yamlConfig("config_validation/baseline.yml") as YamlConfig
    private val defaultExcludePatterns = DEFAULT_PROPERTY_EXCLUDES.toSet()
    private val subject = InvalidPropertiesConfigValidator(baseline, deprecatedProperties, defaultExcludePatterns)

    @Test
    fun `passes for same config test`() {
        val result = subject.validate(baseline)
        assertThat(result).isEmpty()
    }

    @Test
    fun `passes for properties which may appear on rules and rule sets but may be not present in default config`() {
        val result = subject.validate(yamlConfig("config_validation/default-excluded-properties.yml"))
        assertThat(result).isEmpty()
    }

    @Test
    fun `passes for rules defined with rule id`() {
        val result = subject.validate(yamlConfig("config_validation/rule-id.yml"))
        assertThat(result).isEmpty()
    }

    @Test
    fun `reports different rule set name`() {
        val result = subject.validate(yamlConfig("config_validation/other-ruleset-name.yml"))
        assertThat(result).contains(propertyDoesNotExists("code-smell"))
    }

    @Test
    fun `reports different nested property names`() {
        val result = subject.validate(yamlConfig("config_validation/other-nested-property-names.yml"))

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
        val result = subject.validate(yamlConfig("config_validation/no-nested-config.yml"))
        assertThat(result).contains(
            nestedConfigurationExpected("complexity"),
            nestedConfigurationExpected("style>WildcardImport")
        )
    }

    @Test
    fun `reports unexpected nested configs`() {
        // note that the baseline config is now the config to validate
        val subject = InvalidPropertiesConfigValidator(
            yamlConfig("config_validation/no-value.yml") as YamlConfig,
            deprecatedProperties,
            defaultExcludePatterns
        )
        val result = subject.validate(baseline)
        assertThat(result).contains(
            unexpectedNestedConfiguration("style"),
            unexpectedNestedConfiguration("comments")
        )
    }

    @Test
    fun `does not report missing property when it is deprecated`() {
        val result = subject.validate(yamlConfig("config_validation/deprecated-properties.yml"))
        assertThat(result).isEmpty()
    }

    @Test
    fun `reports a string that should be an array as an error`() {
        val config = yamlConfigFromContent(
            """
                style:
                  MagicNumber:
                    ignoreNumbers: '-1,0,1,2'
            """.trimIndent()
        )

        val result = subject.validate(config)

        assertThat(result).anySatisfy { notification ->
            assertThat(notification.message)
                .contains("style>MagicNumber>ignoreNumbers")
                .contains("should be a YAML array instead of a String")
            assertThat(notification.level).isEqualTo(Notification.Level.Error)
        }
    }

    @Nested
    inner class `validate composite configurations` {

        @Test
        fun `passes for same left, right and baseline config`() {
            val result = CompositeConfig(baseline, baseline).validate(baseline, emptySet())
            assertThat(result).isEmpty()
        }

        @Test
        fun `passes for empty configs`() {
            val result = CompositeConfig(Config.empty, Config.empty).validate(baseline, emptySet())
            assertThat(result).isEmpty()
        }

        @Test
        fun `finds accumulated errors`() {
            val result = CompositeConfig(
                yamlConfig("config_validation/other-nested-property-names.yml"),
                yamlConfig("config_validation/no-nested-config.yml")
            ).validate(baseline, emptySet())

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

        private fun patterns(str: String) = setOf(str.toRegex())

        @Test
        fun `does not report any complexity properties`() {
            val subject = InvalidPropertiesConfigValidator(baseline, deprecatedProperties, patterns("complexity"))

            val result = subject.validate(
                yamlConfig("config_validation/other-nested-property-names.yml") as YamlConfig,
            )
            assertThat(result).isEmpty()
        }

        @Test
        fun `does not report 'complexity_LargeClass_howMany'`() {
            val subject = InvalidPropertiesConfigValidator(baseline, deprecatedProperties, patterns(".*>.*>howMany"))
            val result = subject.validate(
                yamlConfig("config_validation/other-nested-property-names.yml") as YamlConfig
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
            val subject = InvalidPropertiesConfigValidator(baseline, deprecatedProperties, patterns(".*>InnerMap"))
            val result = subject.validate(
                yamlConfig("config_validation/other-nested-property-names.yml") as YamlConfig
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
}
