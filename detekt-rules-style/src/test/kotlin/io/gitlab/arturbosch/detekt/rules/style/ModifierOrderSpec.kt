package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ModifierOrderSpec : Spek({
    val subject by memoized { ModifierOrder(Config.empty) }

    describe("ModifierOrder rule") {

        context("kt classes with modifiers") {
            val bad1 = "data internal class Test(val test: String)"
            val bad2 = "actual private class Test(val test: String)"
            val bad3 = "annotation expect class Test"

            it("should report incorrectly ordered modifiers") {
                assertThat(subject.compileAndLint(bad1)).hasSize(1)
                assertThat(subject.lint(bad2)).hasSize(1)
                assertThat(subject.lint(bad3)).hasSize(1)
            }

            it("does not report correctly ordered modifiers") {
                assertThat(subject.compileAndLint("internal data class Test(val test: String)")).isEmpty()
                assertThat(subject.lint("private actual class Test(val test: String)")).isEmpty()
                assertThat(subject.lint("expect annotation class Test")).isEmpty()
            }

            it("should not report issues if inactive") {
                val rule = ModifierOrder(TestConfig(mapOf(Config.ACTIVE_KEY to "false")))
                assertThat(rule.compileAndLint(bad1)).isEmpty()
                assertThat(rule.lint(bad2)).isEmpty()
                assertThat(rule.lint(bad3)).isEmpty()
            }
        }

        context("a kt parameter with modifiers") {

            it("should report wrongly ordered modifiers") {
                val code = "lateinit internal var test: String"
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("should not report correctly ordered modifiers") {
                val code = "internal lateinit var test: String"
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        context("an overridden function") {

            it("should report incorrectly ordered modifiers") {
                val code = """
                    abstract class A {
                        abstract fun test()
                    }
                    abstract class Test : A() {
                        override open fun test() {}
                    }"""
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("should not report correctly ordered modifiers") {
                val code = """
                    abstract class A {
                        abstract fun test()
                    }
                    abstract class Test : A() {
                        override fun test() {}
                    }"""
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        context("a tailrec function") {

            it("should report incorrectly ordered modifiers") {
                val code = """
                    public class A {
                        tailrec private fun foo(x: Double = 1.0): Double = 1.0
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("should not report correctly ordered modifiers") {
                val code = """
                    public class A {
                        private tailrec fun foo(x: Double = 1.0): Double = 1.0
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        context("a vararg argument") {

            it("should report incorrectly ordered modifiers") {
                val code = "class Foo(vararg private val strings: String) {}"
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("should not report correctly ordered modifiers") {
                val code = "class Foo(private vararg val strings: String) {}"
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        context("fun interface") {

            it("should not report correctly ordered modifiers") {
                val code = """
                    private fun interface LoadMoreCallback {
                        fun loadMore(): Boolean
                    }
                """.trimIndent()
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }
    }
})
