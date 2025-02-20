package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EqualsOnSignatureLineSpec {
    val subject = EqualsOnSignatureLine(Config.empty)

    @Nested
    inner class `with expression syntax and without a return type` {
        @Test
        fun `reports when the equals is on a new line`() {
            val findings = subject.lint(
                """
                    fun foo()
                        = 1
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when the equals is on the same line`() {
            val findings = subject.lint(
                """
                    fun foo() = 1
                    
                    fun bar() =
                        2
                """.trimIndent()
            )
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `with expression syntax and with a return type` {
        @Test
        fun `reports when the equals is on a new line`() {
            val findings = subject.lint(
                """
                    fun one(): Int
                        = 1
                    
                    fun two(
                        foo: String
                    )
                        = 2
                    
                    fun three(
                        foo: String
                    ): Int
                        = 3
                """.trimIndent()
            )
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `does not report when the equals is on the same line`() {
            val findings = subject.lint(
                """
                    fun one(): Int =
                        1
                    
                    fun two()
                        : Int =
                        2
                    
                    fun three():
                        Int =
                        3
                    
                    fun four(
                        foo: String
                    ): Int =
                        4
                    
                    fun five(
                        foo: String
                    )
                    : Int =
                        5
                    
                    fun six(
                        foo: String
                    )
                    :
                    Int =
                        6
                """.trimIndent()
            )
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `with expression syntax and with a where clause` {
        @Test
        fun `reports when the equals is on a new line`() {
            val findings = subject.lint(
                """
                    fun <V> one(): Int where V : Number
                        = 1
                    
                    fun <V> two(
                        foo: String
                    ) where V : Number
                        = 2
                    
                    fun <V> three(
                        foo: String
                    ): Int
                        where V : Number
                        = 3
                """.trimIndent()
            )
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `does not report when the equals is on the same line`() {
            val findings = subject.lint(
                """
                    fun <V> one(): Int where V : Number =
                        1
                    
                    fun <V> two() : Int
                        where V : Number =
                        2
                """.trimIndent()
            )
            assertThat(findings).isEmpty()
        }
    }

    @Test
    fun `does not report non-expression functions`() {
        val findings = subject.lint(
            """
                fun foo() {
                }
                
                fun bar()
                {
                }
                
                fun baz()
                :
                Unit
                {
                }
            """.trimIndent()
        )
        assertThat(findings).isEmpty()
    }
}
