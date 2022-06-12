package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
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
            assertThat(subject.compileAndLint(bad1)).hasSize(1)
            assertThat(subject.lint(bad2)).hasSize(1)
            assertThat(subject.lint(bad3)).hasSize(1)
        }

        @Test
        fun `does not report correctly ordered modifiers`() {
            assertThat(subject.compileAndLint("internal data class Test(val test: String)")).isEmpty()
            assertThat(subject.lint("private actual class Test(val test: String)")).isEmpty()
            assertThat(subject.lint("expect annotation class Test")).isEmpty()
        }

        @Test
        fun `should not report issues if inactive`() {
            val rule = ModifierOrder(TestConfig(mapOf(Config.ACTIVE_KEY to "false")))
            assertThat(rule.compileAndLint(bad1)).isEmpty()
            assertThat(rule.lint(bad2)).isEmpty()
            assertThat(rule.lint(bad3)).isEmpty()
        }
    }

    @Nested
    inner class `a kt parameter with modifiers` {

        @Test
        fun `should report wrongly ordered modifiers`() {
            val code = "lateinit internal var test: String"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `should not report correctly ordered modifiers`() {
            val code = "internal lateinit var test: String"
            assertThat(subject.compileAndLint(code)).isEmpty()
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
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
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
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
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
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `should not report correctly ordered modifiers`() {
            val code = """
                public class A {
                    private tailrec fun foo(x: Double = 1.0): Double = 1.0
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `a vararg argument` {

        @Test
        fun `should report incorrectly ordered modifiers`() {
            val code = "class Foo(vararg private val strings: String) {}"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `should not report correctly ordered modifiers`() {
            val code = "class Foo(private vararg val strings: String) {}"
            assertThat(subject.compileAndLint(code)).isEmpty()
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
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `value class` {

        @Test
        fun `should not report correctly ordered modifiers`() {
            val code = """
                @JvmInline
                private value class Foo(val bar: Int)
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
