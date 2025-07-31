package dev.detekt.generator

import com.beust.jcommander.JCommander
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GeneratorArgsSpec {
    @Nested
    inner class TextReplacements {

        private fun parse(vararg args: String): GeneratorArgs {
            val options = GeneratorArgs()
            val parser = JCommander(options)
            parser.parse("-i", ".", *args)
            return options
        }

        @Test
        fun noReplacements() {
            val options = parse()

            assertThat(options.textReplacements).isEmpty()
        }

        @Test
        fun simpleReplacement() {
            val options = parse("--replace", "foo=bar")

            val expected = mapOf("foo" to "bar")
            assertThat(options.textReplacements).containsExactlyEntriesOf(expected)
        }

        @Test
        fun simpleReplacementShortcut() {
            val options = parse("-r", "foo=bar")

            val expected = mapOf("foo" to "bar")
            assertThat(options.textReplacements).containsExactlyEntriesOf(expected)
        }

        @Test
        fun emptyReplacementValue() {
            val options = parse("--replace", "foo=")

            val expected = mapOf("foo" to "")
            assertThat(options.textReplacements).containsExactlyEntriesOf(expected)
        }

        @Test
        fun multipleReplacements() {
            val options = parse("--replace", "foo=bar", "--replace", "faz=baz")

            val expected = mapOf(
                "foo" to "bar",
                "faz" to "baz"
            )
            assertThat(options.textReplacements).containsExactlyEntriesOf(expected)
        }
    }
}
