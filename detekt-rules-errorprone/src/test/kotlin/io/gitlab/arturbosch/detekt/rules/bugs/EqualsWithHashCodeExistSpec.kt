package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EqualsWithHashCodeExistSpec {
    private val subject = EqualsWithHashCodeExist(Config.empty)

    @Nested
    inner class `some classes with equals() and hashCode() functions` {

        @Test
        fun `reports hashCode() without equals() function`() {
            val code = """
            class A {
                override fun hashCode(): Int { return super.hashCode() }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports equals() without hashCode() function`() {
            val code = """
            class A {
                override fun equals(other: Any?): Boolean { return super.equals(other) }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a different equals() function signature`() {
            val code = """
            class A {
                fun equals(other: Any?, i: Int): Boolean { return super.equals(other) }
                override fun hashCode(): Int { return super.hashCode() }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a different hashcode() function signature`() {
            val code = """
            class A {
                override fun equals(other: Any?): Boolean { return super.equals(other) }
                fun hashCode(i: Int): Int { return super.hashCode() }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a different overridden equals() function signature`() {
            val code = """
            interface I {
                fun equals(other: Any?, i: Int): Boolean
            }
            
            class A : I {
                override fun equals(other: Any?, i: Int): Boolean { return super.equals(other) }
                override fun hashCode(): Int { return super.hashCode() }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports a different overridden hashCode() function signature`() {
            val code = """
            interface I {
                fun hashCode(i: Int): Int
            }
            
            class A : I {
                override fun equals(other: Any?): Boolean { return super.equals(other) }
                override fun hashCode(i: Int): Int { return super.hashCode() }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report equals() with hashCode() function`() {
            val code = """
            class A {
                override fun equals(other: Any?): Boolean { return super.equals(other) }
                override fun hashCode(): Int { return super.hashCode() }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report when using nullable Any`() {
            val code = """
            class A {
                override fun equals(other: kotlin.Any?): Boolean { return super.equals(other) }
                override fun hashCode(): Int { return super.hashCode() }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `a data class` {

        @Test
        fun `does not report equals() or hashcode() violation on data class`() {
            val code = """
            data class EqualsData(val i: Int) {
                override fun equals(other: Any?): Boolean {
                    return super.equals(other)
                }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
