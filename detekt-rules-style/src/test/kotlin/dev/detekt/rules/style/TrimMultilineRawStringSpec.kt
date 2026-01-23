package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TrimMultilineRawStringSpec {
    val subject = TrimMultilineRawString(Config.Empty)

    @Test
    fun `raises multiline raw strings without trim`() {
        val code = """
            val a = $TQ
            Hello world!
            $TQ
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `doesn't raise multiline raw strings with custom default configured trimIndent method`() {
        fun String.trimIndent() = this
        val code = """
            val a = $TQ
            Hello world!
            $TQ.trimIndent()
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `doesn't raise multiline raw strings with custom configured customTrim method`() {
        val code = """
            fun String.customTrim() = this
            val a = $TQ
            Hello world!
            $TQ.customTrim()
        """.trimIndent()
        val findings = TrimMultilineRawString(TestConfig("trimmingMethods" to listOf("customTrim")))
            .lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `raises multiline raw strings with length`() {
        val code = """
            val a = $TQ
            Hello world!
            $TQ.length
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `doesn't raise multiline raw strings with trimIndent`() {
        val code = """
            val a = $TQ
            Hello world!
            $TQ.trimIndent()
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `doesn't raise multiline raw strings with trimMargin`() {
        val code = """
            val a = $TQ
            |Hello world!
            $TQ.trimMargin()
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `doesn't raise multiline raw strings with trimMargin with parameter`() {
        val code = """
            val a = $TQ
            >Hello world!
            $TQ.trimMargin(">")
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `don't raise one line raw strings`() {
        val code = """
            val a = ${TQ}Hello world!$TQ
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `doesn't raise if it is not a raw string`() {
        val code = """
            val a = "Hello world!"
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `doesn't raise if it is not a raw string - multiline`() {
        val code = """
            val a = "Hello ${'$'}{
                "cruel"
            } world!"
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `doesn't raise on constant`() {
        val code = """
            object O {
                const val s =
                    $TQ
                        Given something
                        When something
                        Then something
                    $TQ
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `doesn't raise on annotation entry arguments`() {
        val code = """
            annotation class DisplayName(val s: String)
            @DisplayName(
                $TQ
                    Given something
                    When something
                    Then something
                $TQ
            )
            class Foo
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `doesn't raise on annotation constructor parameters`() {
        val code = """
            annotation class DisplayName(
                val s: String =
                    $TQ
                        Given something
                        When something
                        Then something
                    $TQ
            )
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `raises on function arguments`() {
        val code = """
            fun foo(s: String) {}
            val bar = foo(
                $TQ
                    Given something
                    When something
                    Then something
                $TQ
            )
            class Foo
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `raises on class constructor parameters`() {
        val code = """
            class Foo(
                val s: String =
                    $TQ
                        Given something
                        When something
                        Then something
                    $TQ
            )
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }
}

private const val TQ = "\"\"\""
