package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ModifierOrderSpec {
    val subject = ModifierOrder(Config.empty)

    @Nested
    inner class `kt classes with modifiers` {
        val bad1 = "data internal class Test(val test: String)"
        val bad2 = "actual private class Test(val test: String)"
        val bad3 = "annotation expect class Test"

        @Test
        fun `should report incorrectly ordered modifiers`() {
            subject.lint(bad1).let {
                assertThat(it).singleElement().hasMessage("Modifier order should be: internal data")
            }
            subject.lint(bad2, compile = false).let {
                assertThat(it).singleElement().hasMessage("Modifier order should be: private actual")
            }
            subject.lint(bad3, compile = false).let {
                assertThat(it).singleElement().hasMessage("Modifier order should be: expect annotation")
            }
        }

        @Test
        fun `does not report correctly ordered modifiers`() {
            assertThat(subject.lint("internal data class Test(val test: String)")).isEmpty()
            assertThat(subject.lint("private actual class Test(val test: String)", compile = false)).isEmpty()
            assertThat(subject.lint("expect annotation class Test", compile = false)).isEmpty()
            assertThat(subject.lint("private /* comment */ data class Test(val test: String)")).isEmpty()
        }
    }

    @Nested
    inner class `a kt parameter with modifiers` {

        @Test
        fun `should report wrongly ordered modifiers`() {
            val code = "lateinit internal var test: String"
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should not report correctly ordered modifiers`() {
            val code = "internal lateinit var test: String"
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `an overridden function` {

        @Test
        fun `should report incorrectly ordered modifiers`() {
            val code = """
                abstract class A {
                    abstract fun test()
                }
                abstract class Test : A() {
                    override open fun test() {}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should not report correctly ordered modifiers`() {
            val code = """
                abstract class A {
                    abstract fun test()
                }
                abstract class Test : A() {
                    override fun test() {}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `a tailrec function` {

        @Test
        fun `should report incorrectly ordered modifiers`() {
            val code = """
                public class A {
                    tailrec private fun foo(x: Double = 1.0): Double = 1.0
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should not report correctly ordered modifiers`() {
            val code = """
                public class A {
                    private tailrec fun foo(x: Double = 1.0): Double = 1.0
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `a vararg argument` {

        @Test
        fun `should report incorrectly ordered modifiers`() {
            val code = "class Foo(vararg private val strings: String) {}"
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should not report correctly ordered modifiers`() {
            val code = "class Foo(private vararg val strings: String) {}"
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `fun interface` {

        @Test
        fun `should not report correctly ordered modifiers`() {
            val code = """
                private fun interface LoadMoreCallback {
                    fun loadMore(): Boolean
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `value class` {

        @Test
        fun `should not report correctly ordered modifiers`() {
            val code = """
                @JvmInline
                private value class Foo(val bar: Int)
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
}
