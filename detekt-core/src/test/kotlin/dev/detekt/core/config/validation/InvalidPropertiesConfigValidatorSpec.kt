package dev.detekt.core.config.validation

import dev.detekt.api.Config
import dev.detekt.api.Notification
import dev.detekt.core.config.CompositeConfig
import dev.detekt.core.config.validation.InvalidPropertiesConfigValidator.Companion.findClosestMatch
import dev.detekt.core.config.validation.InvalidPropertiesConfigValidator.Companion.nestedConfigurationExpected
import dev.detekt.core.config.validation.InvalidPropertiesConfigValidator.Companion.propertyDoesNotExists
import dev.detekt.core.config.validation.InvalidPropertiesConfigValidator.Companion.unexpectedNestedConfiguration
import dev.detekt.core.yamlConfig
import dev.detekt.core.yamlConfigFromContent
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
    private val baseline = yamlConfig("config_validation/baseline.yml")
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
        assertThat(result).anySatisfy { notification ->
            assertThat(notification.message).contains("'code-smell' is misspelled or does not exist")
        }
    }

    @Test
    fun `reports different nested property names`() {
        val result = subject.validate(yamlConfig("config_validation/other-nested-property-names.yml"))

        val messages = result.map { it.message }
        assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LongLongMethod'") }
        assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LongParameterList>enabled'") }
        assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LargeClass>howMany'") }
        assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>InnerMap>InnerKey'") }
        assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>InnerMap>Inner2>nestedActive'") }
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
            yamlConfig("config_validation/no-value.yml"),
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
            )
            val messages = result.map { it.message }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LongLongMethod'") }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LongParameterList>enabled'") }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LargeClass>howMany'") }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>InnerMap>InnerKey'") }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>InnerMap>Inner2>nestedActive'") }
        }
    }

    @Nested
    inner class `suggests similar property names` {

        @Test
        fun `suggests similar property name for close matches`() {
            val result = subject.validate(yamlConfig("config_validation/other-nested-property-names.yml"))
            val longLongMethodMessage = result.map { it.message }
                .first { it.contains("'complexity>LongLongMethod'") }

            assertThat(longLongMethodMessage).contains("Did you mean 'LongMethod'?")
        }

        @Test
        fun `lists allowed properties in error message`() {
            val result = subject.validate(yamlConfig("config_validation/other-nested-property-names.yml"))
            val howManyMessage = result.map { it.message }
                .first { it.contains("'complexity>LargeClass>howMany'") }

            assertThat(howManyMessage).contains("Allowed properties: [active, threshold]")
        }

        @Test
        fun `does not suggest when no close match exists`() {
            val result = subject.validate(yamlConfig("config_validation/other-ruleset-name.yml"))
            val codeSmellMessage = result.map { it.message }
                .first { it.contains("'code-smell'") }

            assertThat(codeSmellMessage).doesNotContain("Did you mean")
        }
    }

    @Nested
    inner class `propertyDoesNotExists message format` {

        @Test
        fun `includes suggestion for close match`() {
            val notification = propertyDoesNotExists(
                "complexity>LargeClass>threshhold",
                "threshhold",
                setOf("active", "threshold"),
            )
            assertThat(notification.message).contains("Did you mean 'threshold'?")
            assertThat(notification.message).contains("Allowed properties: [active, threshold]")
        }

        @Test
        fun `omits suggestion when no close match`() {
            val notification = propertyDoesNotExists(
                "complexity>LargeClass>xyz",
                "xyz",
                setOf("active", "threshold"),
            )
            assertThat(notification.message).doesNotContain("Did you mean")
            assertThat(notification.message).contains("Allowed properties: [active, threshold]")
        }
    }

    @Nested
    inner class `find closest match` {

        @Test
        fun `finds closest match for typo`() {
            val result = findClosestMatch("threshhold", setOf("active", "threshold"))
            assertThat(result).isEqualTo("threshold")
        }

        @Test
        fun `finds closest match case insensitively`() {
            val result = findClosestMatch("Active", setOf("active", "threshold"))
            assertThat(result).isEqualTo("active")
        }

        @Test
        fun `returns null when no close match`() {
            val result = findClosestMatch("xyz", setOf("active", "threshold"))
            assertThat(result).isNull()
        }

        @Test
        fun `finds closest match for similar name`() {
            val result = findClosestMatch(
                "LongLongMethod",
                setOf("LongMethod", "LongParameterList", "LargeClass", "InnerMap"),
            )
            assertThat(result).isEqualTo("LongMethod")
        }
    }

    @Nested
    inner class `configure additional exclude paths` {

        private fun patterns(str: String) = setOf(str.toRegex())

        @Test
        fun `does not report any complexity properties`() {
            val subject = InvalidPropertiesConfigValidator(baseline, deprecatedProperties, patterns("complexity"))

            val result = subject.validate(yamlConfig("config_validation/other-nested-property-names.yml"))
            assertThat(result).isEmpty()
        }

        @Test
        fun `does not report 'complexity_LargeClass_howMany'`() {
            val subject = InvalidPropertiesConfigValidator(baseline, deprecatedProperties, patterns(".*>.*>howMany"))
            val result = subject.validate(yamlConfig("config_validation/other-nested-property-names.yml"))

            val messages = result.map { it.message }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LongLongMethod'") }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LongParameterList>enabled'") }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>InnerMap>InnerKey'") }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>InnerMap>Inner2>nestedActive'") }

            assertThat(messages).noneSatisfy { assertThat(it).contains("'complexity>LargeClass>howMany'") }
        }

        @Test
        @DisplayName("does not report .*>InnerMap")
        fun `does not report innerMap`() {
            val subject = InvalidPropertiesConfigValidator(baseline, deprecatedProperties, patterns(".*>InnerMap"))
            val result = subject.validate(yamlConfig("config_validation/other-nested-property-names.yml"))

            val messages = result.map { it.message }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LargeClass>howMany'") }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LongLongMethod'") }
            assertThat(messages).anySatisfy { assertThat(it).contains("'complexity>LongParameterList>enabled'") }

            assertThat(messages).noneSatisfy { assertThat(it).contains("'complexity>InnerMap>InnerKey'") }
            assertThat(messages).noneSatisfy { assertThat(it).contains("'complexity>InnerMap>Inner2>nestedActive'") }
        }
    }
}
