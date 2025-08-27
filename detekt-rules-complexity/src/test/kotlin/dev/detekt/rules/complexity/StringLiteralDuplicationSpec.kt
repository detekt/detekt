package dev.detekt.rules.complexity

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

private const val IGNORE_ANNOTATION = "ignoreAnnotation"
private const val IGNORE_STRINGS_REGEX = "ignoreStringsRegex"
private const val ALLOWED_WITH_LENGTH_LESS_THAN = "allowedWithLengthLessThan"

class StringLiteralDuplicationSpec {

    val subject = StringLiteralDuplication(TestConfig("allowedDuplications" to 2))

    @Nested
    inner class `many hardcoded strings` {

        @Test
        fun `reports 3 equal hardcoded strings`() {
            val code = """
                class Duplication {
                    var s1 = "lorem"
                    fun f(s: String = "lorem") {
                        s1.equals("lorem")
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report 2 equal hardcoded strings`() {
            val code = """val str = "lorem" + "lorem" + "ipsum""""
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `strings in annotations` {

        val code = """
            @Suppress("unused")
            class A
            @Suppress("unused")
            class B
            @Suppress("unused")
            class C
        """.trimIndent()

        @Test
        fun `does not report strings in annotations`() {
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports strings in annotations according to config`() {
            val config = TestConfig(IGNORE_ANNOTATION to "false", "allowedDuplications" to 2)
            assertFindingWithConfig(code, config, 1)
        }
    }

    @Nested
    inner class `strings with less than 5 characters` {

        private val code = """val str = "amet" + "amet" + "amet""""

        @Test
        fun `does not report strings with 4 characters`() {
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports string with 4 characters`() {
            val config = TestConfig(
                ALLOWED_WITH_LENGTH_LESS_THAN to 0,
                "allowedDuplications" to 2
            )
            assertFindingWithConfig(code, config, 1)
        }
    }

    @Nested
    inner class `strings with allowedWithLengthLessThan configured` {
        private val code = """val str = "amet" + "amet" + "amet""""

        @Test
        fun `reports string with 4 characters`() {
            val config = TestConfig(
                ALLOWED_WITH_LENGTH_LESS_THAN to 4,
            )
            assertFindingWithConfig(code, config, 1)
        }

        @Test
        fun `does not report string with 4 characters with allowStringWithLength 4`() {
            val config = TestConfig(ALLOWED_WITH_LENGTH_LESS_THAN to 5)
            assertFindingWithConfig(code, config, 0)
        }
    }

    @Nested
    inner class `strings with values to match for the regex` {

        private val regexTestingCode = """
            val str1 = "lorem" + "lorem" + "lorem"
            val str2 = "ipsum" + "ipsum" + "ipsum"
        """.trimIndent()

        @Test
        fun `does not report lorem or ipsum according to config in regex`() {
            val code = """
                val str1 = "lorem" + "lorem" + "lorem"
                val str2 = "ipsum" + "ipsum" + "ipsum"
            """.trimIndent()
            val config = TestConfig(IGNORE_STRINGS_REGEX to "(lorem|ipsum)")
            assertFindingWithConfig(code, config, 0)
        }

        @Test
        fun `should fail with invalid regex`() {
            val config = TestConfig(IGNORE_STRINGS_REGEX to "*lorem")
            assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                StringLiteralDuplication(config).lint(regexTestingCode)
            }
        }
    }

    @Nested
    inner class `saves string literal references` {

        @Test
        fun `reports 3 locations for 'lorem'`() {
            val code = """
                class Duplication {
                    var s1 = "lorem"
                    fun f(s: String = "lorem") {
                        s1.equals("lorem")
                    }
                }
            """.trimIndent()
            val finding = subject.lint(code)[0]
            val locations = finding.references.map { it.location } + finding.entity.location
            assertThat(locations).hasSize(3)
        }
    }

    @Nested
    inner class `multiline strings with string interpolation` {

        @Test
        fun `does not report duplicated parts in multiline strings`() {
            val code = """
                // does not report because it treats the multiline string parts as one string
                val str = ""${'"'}
                    |
                    |
                    |
                    ""${'"'}
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Test
    fun `should suppress StringLiteralDuplication on class level`() {
        @Suppress("UnusedEquals")
        val code = """
            @Suppress("StringLiteralDuplication")
            class Duplication {
                var s1 = "lorem"
                fun f(s: String = "lorem") {
                    s1 == "lorem"
                }
            }
        """.trimIndent()

        assertThat(StringLiteralDuplication(Config.empty).lint(code)).isEmpty()
    }
}

private fun assertFindingWithConfig(code: String, config: TestConfig, expected: Int) {
    val findings = StringLiteralDuplication(config).lint(code)
    assertThat(findings).hasSize(expected)
}
