package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UnnecessaryBackticksSpec {
    val subject = UnnecessaryBackticks(Config.empty)

    @Nested
    inner class `Reports UnnecessaryInnerClass Rule` {
        @Test
        fun `class`() {
            val code = """
                class `Foo` {
                    val x: `Foo` = `Foo`()
                    val y = ::`Foo`
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(4)
        }

        @Test
        fun function() {
            val code = """
                fun `foo`() = 1
                val x = `foo`()
                val y = ::`foo`
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(3)
        }

        @Test
        fun property() {
            val code = """
                val `foo` = ""
                val x = `foo`
                val y = ::`foo`
                val z = `foo`.length
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(4)
        }

        @Test
        fun import() {
            val code = """
                import kotlin.`let`
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `in string template`() {
            val code = """
                val foo = ""
                val x = "${'$'}`foo`"
                val y = "${'$'}`foo` bar"
                val z = "${'$'}{`foo`}bar"
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `Does not report UnnecessaryInnerClass Rule` {
        @Test
        fun `class with spaces`() {
            val code = """
                class `Foo Bar`
                val x: `Foo Bar` = `Foo Bar`()
                val y = ::`Foo Bar`
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `function with spaces`() {
            val code = """
                fun `foo bar`() = 1
                val x = `foo bar`()
                val y = ::`foo bar`
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `property with spaces`() {
            val code = """
                val `foo bar` = ""
                val x = `foo bar`
                val y = ::`foo bar`
                val z = `foo bar`.length
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun keyword() {
            val code = """
                val `is` = 1
                val `fun` = 2
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun underscore() {
            val code = """
                val `_` = 1
                val `__` = 2
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun import() {
            val code = """
                package test
                import test.`Foo Bar`
                class `Foo Bar`
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `in string template`() {
            val code = """
                val foo = ""
                val x = "${'$'}`foo`bar"
                val `bar baz` = ""
                val y = "${'$'}`bar baz`"
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
}
