package dev.detekt.generator.printer

import dev.detekt.api.valuesWithReason
import dev.detekt.generator.collection.Configuration
import dev.detekt.generator.collection.DefaultValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ConfigurationsPrinterSpec {
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
            val actual = ConfigurationsPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``true``)""")
        }

        @Test
        fun `int default`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of(99))
            val actual = ConfigurationsPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``99``)""")
        }

        @Test
        fun `int default with groupings`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of(99_999))
            val actual = ConfigurationsPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``99999``)""")
        }

        @Test
        fun `string default`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of("abc"))
            val actual = ConfigurationsPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``'abc'``)""")
        }

        @Test
        fun `string list default`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of(listOf("a", "b", "c")))
            val actual = ConfigurationsPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``['a', 'b', 'c']``)""")
        }

        @Test
        fun `empty string list default`() {
            val config = configTemplate.copy(defaultValue = DefaultValue.of(emptyList()))
            val actual = ConfigurationsPrinter.print(listOf(config))
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
            val actual = ConfigurationsPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``['a', 'b', 'c']``)""")
        }

        @Test
        fun `with android default`() {
            val config = configTemplate.copy(
                defaultValue = DefaultValue.of(99),
                defaultAndroidValue = DefaultValue.of(100)
            )
            val actual = ConfigurationsPrinter.print(listOf(config))
            assertThat(actual).contains("""* ``configName`` (default: ``99``) (android default: ``100``)""")
        }
    }

    @Nested
    inner class DeprecatedProperties {
        private val config = configTemplate.copy(deprecated = "Use something else instead")
        private val actual = ConfigurationsPrinter.print(listOf(config))

        @Test
        fun `contain deprecation information`() {
            assertThat(actual).contains("""**Deprecated**: Use something else instead""")
        }

        @Test
        fun `have strike through`() {
            assertThat(actual).contains("""~~``configName``~~""")
        }
    }

    @Nested
    inner class MacroExpansion {
        @Test
        fun `print expands macros in configuration description`() {
            // GIVEN
            val config = Configuration(
                name = "methods",
                description = "List of methods. {{FUNCTION_MATCHER_DOCS}}",
                defaultValue = DefaultValue.of(emptyList<String>()),
                defaultAndroidValue = null,
                deprecated = null
            )

            // WHEN
            val actual = ConfigurationsPrinter.print(listOf(config))

            // THEN
            assertThat(actual)
                .doesNotContain("{{FUNCTION_MATCHER_DOCS}}")
                .contains("Methods can be defined without full signature")
                .contains("java.time.LocalDate.now")
        }

        @Test
        fun `print preserves non-macro descriptions`() {
            // GIVEN
            val config = Configuration(
                name = "normalConfig",
                description = "This is a normal description without macros",
                defaultValue = DefaultValue.of(true),
                defaultAndroidValue = null,
                deprecated = null
            )

            // WHEN
            val actual = ConfigurationsPrinter.print(listOf(config))

            // THEN
            assertThat(actual).contains("This is a normal description without macros")
        }

        @Test
        fun `print handles multiple configurations with same macro`() {
            // GIVEN
            val config1 = Configuration(
                name = "methods1",
                description = "First: {{FUNCTION_MATCHER_DOCS}}",
                defaultValue = DefaultValue.of(emptyList<String>()),
                defaultAndroidValue = null,
                deprecated = null
            )
            val config2 = Configuration(
                name = "methods2",
                description = "Second: {{FUNCTION_MATCHER_DOCS}}",
                defaultValue = DefaultValue.of(emptyList<String>()),
                defaultAndroidValue = null,
                deprecated = null
            )

            // WHEN
            val actual = ConfigurationsPrinter.print(listOf(config1, config2))

            // THEN
            assertThat(actual).doesNotContain("{{FUNCTION_MATCHER_DOCS}}")
            // Both should have expanded text
            val matchCount = actual.split("Methods can be defined").size - 1
            assertThat(matchCount).isEqualTo(2)
        }
    }
}
