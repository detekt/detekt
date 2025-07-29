package io.gitlab.arturbosch.detekt.rules

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ThrowExtensionsSpec {

    @Nested
    inner class `is enclosed by conditional statement` {
        @Test
        fun `is true for if statement on same line`() {
            val code = """
                fun test() {
                    if (i == 1) throw IllegalArgumentException()
                }
            """.trimIndent()

            verifyThrowExpression(code) {
                assertThat(isEnclosedByConditionalStatement()).isTrue()
            }
        }

        @Test
        fun `is true for if statement on separate line`() {
            val code = """
                fun test() {
                    if (i == 1)
                        throw IllegalArgumentException()
                }
            """.trimIndent()

            verifyThrowExpression(code) {
                assertThat(isEnclosedByConditionalStatement()).isTrue()
            }
        }

        @Test
        fun `is true for if statement on in block`() {
            val code = """
                fun test() {
                    if (i == 1) {
                        println("message")
                        throw IllegalArgumentException()
                    }
                }
            """.trimIndent()

            verifyThrowExpression(code) {
                assertThat(isEnclosedByConditionalStatement()).isTrue()
            }
        }

        @Test
        fun `is false if thrown unconditionally`() {
            val code = """
                fun test() {
                    throw IllegalArgumentException()
                }
            """.trimIndent()

            verifyThrowExpression(code) {
                assertThat(isEnclosedByConditionalStatement()).isFalse()
            }
        }

        @Test
        fun `is false for when statement`() {
            val code = """
                fun test(a: Int) {
                    when (a) {
                        1 -> throw IllegalArgumentException()
                        2 -> println("2")
                        else -> println("other")
                    }
                }
            """.trimIndent()

            verifyThrowExpression(code) {
                assertThat(isEnclosedByConditionalStatement()).isFalse()
            }
        }

        @Test
        fun `is true in else clause`() {
            val code = """
                fun test(a: Int) {
                    if (a == 2) println("2") else throw IllegalArgumentException()
                }
            """.trimIndent()

            verifyThrowExpression(code) {
                assertThat(isEnclosedByConditionalStatement()).isTrue()
            }
        }

        @Test
        fun `is true in else clause with curly braces`() {
            val code = """
                fun test(a: Int) {
                    if (a == 2) {
                        println("2")
                    } else {
                        throw IllegalArgumentException()
                    }
                }
            """.trimIndent()

            verifyThrowExpression(code) {
                assertThat(isEnclosedByConditionalStatement()).isTrue()
            }
        }
    }

    private fun verifyThrowExpression(
        @Language("kotlin") code: String,
        throwingAssertions: KtThrowExpression.() -> Unit,
    ) {
        val ktFile = compileContentForTest(code)
        val ktThrowExpression = ktFile.findDescendantOfType<KtThrowExpression>()
        assertThat(ktThrowExpression)
            .withFailMessage("no throw expression found")
            .isNotNull
        throwingAssertions(ktThrowExpression!!)
    }
}
