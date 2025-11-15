package dev.detekt.generator.collection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TextReplacementMacroSpec {

    @Test
    fun `expandMacro returns macro content for valid macro name`() {
        // GIVEN
        val input = "{{FUNCTION_MATCHER_DOCS}}"
        val processor = TextReplacementMacro()

        // WHEN
        val result = processor.expand(input)

        // THEN
        assertThat(result)
            .contains("Methods can be defined without full signature")
            .contains("java.time.LocalDate.now")
            .contains("extension function")
            .contains("<init>")
            .contains("vararg")
            .contains("companion object")
    }

    @Test
    fun `expandMacro preserves text without macros`() {
        // GIVEN
        val input = "This is regular text without any macros."
        val processor = TextReplacementMacro()

        // WHEN
        val result = processor.expand(input)

        // THEN
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun `expandMacro handles multiple macros in same text`() {
        // GIVEN
        val input = "Start {{FUNCTION_MATCHER_DOCS}} middle {{FUNCTION_MATCHER_DOCS}} end"
        val processor = TextReplacementMacro()

        // WHEN
        val result = processor.expand(input)

        // THEN
        assertThat(result)
            .contains("Start")
            .contains("middle")
            .contains("end")
        // Should contain the expanded text twice
        val matchCount = result.split("Methods can be defined").size - 1
        assertThat(matchCount).isEqualTo(2)
    }

    @Test
    fun `expandMacro throws exception for undefined macro`() {
        // GIVEN
        val input = "Some text {{UNDEFINED_MACRO}} more text"
        val processor = TextReplacementMacro()

        // WHEN/THEN
        try {
            processor.expand(input)
            throw AssertionError("Expected IllegalArgumentException to be thrown")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).contains("UNDEFINED_MACRO")
        }
    }

    @Test
    fun `expandMacro handles macro adjacent to other text`() {
        // GIVEN
        val input = "Prefix:{{FUNCTION_MATCHER_DOCS}}:Suffix"
        val processor = TextReplacementMacro()

        // WHEN
        val result = processor.expand(input)

        // THEN
        assertThat(result)
            .startsWith("Prefix:")
            .endsWith(":Suffix")
            .contains("Methods can be defined")
    }

    @Test
    fun `expandMacro handles empty input`() {
        // GIVEN
        val input = ""
        val processor = TextReplacementMacro()

        // WHEN
        val result = processor.expand(input)

        // THEN
        assertThat(result).isEmpty()
    }

    @Test
    fun `availableMacros returns list of defined macros`() {
        // GIVEN
        val processor = TextReplacementMacro()

        // WHEN
        val macros = processor.availableMacros()

        // THEN
        assertThat(macros)
            .contains("FUNCTION_MATCHER_DOCS")
            .hasSizeGreaterThanOrEqualTo(1)
    }
}
