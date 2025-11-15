package dev.detekt.generator.integration

import dev.detekt.generator.collection.Configuration
import dev.detekt.generator.collection.DefaultValue
import dev.detekt.generator.printer.ConfigurationsPrinter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MacroExpansionIntegrationSpec {

    @Test
    fun `documentation generation produces identical output with macros`() {
        // GIVEN
        val config = Configuration(
            name = "methods",
            description = "List of fully qualified method signatures which are forbidden. {{FUNCTION_MATCHER_DOCS}}",
            defaultValue = DefaultValue.of(emptyList<String>()),
            defaultAndroidValue = null,
            deprecated = null
        )

        // WHEN - Generate with macro
        val generatedMarkdown = ConfigurationsPrinter.print(listOf(config))

        // THEN - Macro should be expanded with full documentation
        assertThat(generatedMarkdown)
            .contains("#### Configuration options:")
            .contains("``methods`` (default: ``[]``)")
            .contains("List of fully qualified method signatures which are forbidden.")
            .contains("Methods can be defined without full signature")
            .contains("java.time.LocalDate.now")
            .contains("extension function")
            .contains("vararg")
            .contains("companion object")
            .doesNotContain("{{FUNCTION_MATCHER_DOCS}}")
    }

    @Test
    fun `macro expansion works in end-to-end documentation generation`() {
        // GIVEN
        val configs = listOf(
            Configuration(
                name = "forbiddenMethods",
                description = "Forbidden methods. {{FUNCTION_MATCHER_DOCS}}",
                defaultValue = DefaultValue.of(emptyList<String>()),
                defaultAndroidValue = null,
                deprecated = null
            ),
            Configuration(
                name = "maxComplexity",
                description = "Maximum complexity allowed",
                defaultValue = DefaultValue.of(10),
                defaultAndroidValue = null,
                deprecated = null
            )
        )

        // WHEN
        val result = ConfigurationsPrinter.print(configs)

        // THEN
        assertThat(result)
            .contains("forbiddenMethods")
            .contains("maxComplexity")
            .contains("Methods can be defined without full signature")
            .contains("Maximum complexity allowed")
            .doesNotContain("{{FUNCTION_MATCHER_DOCS}}")
    }
}
