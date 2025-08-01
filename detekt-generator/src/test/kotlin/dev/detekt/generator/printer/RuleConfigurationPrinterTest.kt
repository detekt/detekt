package dev.detekt.generator.printer

import dev.detekt.api.valuesWithReason
import dev.detekt.generator.collection.Configuration
import dev.detekt.generator.collection.DefaultValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RuleConfigurationPrinterTest {
    private val configTemplate = Configuration(
        name = "configName",
        description = "config description",
        defaultValue = DefaultValue.of(true),
        defaultAndroidValue = null,
        deprecated = null
    )

    @Nested
    inner class DefaultValues {
        @Test
        fun `boolean default`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of(true))
            val actual = RuleConfigurationPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``true``)""")
        }

        @Test
        fun `int default`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of(99))
            val actual = RuleConfigurationPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``99``)""")
        }

        @Test
        fun `int default with groupings`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of(99_999))
            val actual = RuleConfigurationPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``99999``)""")
        }

        @Test
        fun `string default`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of("abc"))
            val actual = RuleConfigurationPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``'abc'``)""")
        }

        @Test
        fun `string list default`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of(listOf("a", "b", "c")))
            val actual = RuleConfigurationPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``['a', 'b', 'c']``)""")
        }

        @Test
        fun `empty string list default`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of(emptyList()))
            val actual = RuleConfigurationPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``[]``)""")
        }

        @Test
        fun `values with reason default`() {
            val config = configTemplate.copy(
                defaultValue = DefaultValue.of(
                    valuesWithReason(
                        "a" to "reason for a",
                        "b" to "reason for b",
                        "c" to null
                    )
                )
            )
            val actual = RuleConfigurationPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``['a', 'b', 'c']``)""")
        }

        @Test
        fun `with android default`() {
            val config = configTemplate.copy(
                defaultValue = DefaultValue.of(99),
                defaultAndroidValue = DefaultValue.of(100)
            )
            val actual = RuleConfigurationPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``99``) (android default: ``100``)""")
        }
    }

    @Nested
    inner class DeprecatedProperties {
        private val config = configTemplate.copy(deprecated = "Use something else instead")
        private val actual = RuleConfigurationPrinter.print(listOf(config))

        @Test
        fun `contain deprecation information`() {
            assertThat(actual).contains("""**Deprecated**: Use something else instead""")
        }

        @Test
        fun `have strike through`() {
            assertThat(actual).contains("""~~``configName``~~""")
        }
    }
}
