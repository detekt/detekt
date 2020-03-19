package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.regex.PatternSyntaxException

class StringLiteralDuplicationSpec : Spek({

    val subject by memoized { StringLiteralDuplication() }

    describe("StringLiteralDuplication rule") {

        context("many hardcoded strings") {

            it("reports 3 equal hardcoded strings") {
                val code = """
                class Duplication {
                    var s1 = "lorem"
                    fun f(s: String = "lorem") {
                        s1.equals("lorem")
                    }
                }"""
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does not report 2 equal hardcoded strings") {
                val code = """val str = "lorem" + "lorem" + "ipsum""""
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        context("strings in annotations") {

            val code = """
                @Suppress("unused")
                class A
                @Suppress("unused")
                class B
                @Suppress("unused")
                class C
            """

            it("does not report strings in annotations") {
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports strings in annotations according to config") {
                val config = TestConfig(mapOf(StringLiteralDuplication.IGNORE_ANNOTATION to "false"))
                assertFindingWithConfig(code, config, 1)
            }
        }

        context("strings with less than 5 characters") {

            val code = """val str = "amet" + "amet" + "amet""""

            it("does not report strings with 4 characters") {
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports string with 4 characters") {
                val config = TestConfig(mapOf(StringLiteralDuplication.EXCLUDE_SHORT_STRING to "false"))
                assertFindingWithConfig(code, config, 1)
            }
        }

        context("strings with values to match for the regex") {

            val regexTestingCode = """
                val str1 = "lorem" + "lorem" + "lorem"
                val str2 = "ipsum" + "ipsum" + "ipsum"
            """

            it("does not report lorem or ipsum according to config in regex") {
                val code = """
                    val str1 = "lorem" + "lorem" + "lorem"
                    val str2 = "ipsum" + "ipsum" + "ipsum"
                """
                val config = TestConfig(mapOf(StringLiteralDuplication.IGNORE_STRINGS_REGEX to "(lorem|ipsum)"))
                assertFindingWithConfig(code, config, 0)
            }

            it("should not fail with invalid regex when disabled") {
                val configValues = mapOf(
                        "active" to "false",
                        StringLiteralDuplication.IGNORE_STRINGS_REGEX to "*lorem"
                )
                val config = TestConfig(configValues)
                assertFindingWithConfig(regexTestingCode, config, 0)
            }

            it("should fail with invalid regex") {
                val config = TestConfig(mapOf(StringLiteralDuplication.IGNORE_STRINGS_REGEX to "*lorem"))
                assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                    StringLiteralDuplication(config).compileAndLint(regexTestingCode)
                }
            }
        }

        describe("saves string literal references") {

            it("reports 3 locations for 'lorem'") {
                val code = """
                class Duplication {
                    var s1 = "lorem"
                    fun f(s: String = "lorem") {
                        s1.equals("lorem")
                    }
                }"""
                val finding = subject.compileAndLint(code)[0]
                val locations = finding.references.map { it.location } + finding.entity.location
                assertThat(locations).hasSize(3)
            }
        }

        describe("multiline strings with string interpolation") {

            it("does not report duplicated parts in multiline strings") {
                val code = """
                    // does not report because it treats the multiline string parts as one string
                    val str = ""${'"'}
                        |
                        |
                        |
                        ""${'"'}
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }
    }
})

private fun assertFindingWithConfig(code: String, config: TestConfig, expected: Int) {
    val findings = StringLiteralDuplication(config).compileAndLint(code)
    assertThat(findings).hasSize(expected)
}
