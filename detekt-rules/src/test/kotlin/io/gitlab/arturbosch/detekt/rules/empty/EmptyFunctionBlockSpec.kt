package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EmptyFunctionBlockSpec : Spek({

    val subject by memoized { EmptyFunctionBlock(Config.empty) }

    describe("EmptyFunctionBlock rule") {

        it("should flag function with protected modifier") {
            val code = """
                class A {
                    protected fun stuff() {}
                }"""
            assertThat(subject.compileAndLint(code)).hasSourceLocation(2, 27)
        }

        it("should not flag function with open modifier") {
            val code = """
                open class A {
                    open fun stuff() {}
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should not flag a default function in an interface") {
            val code = """
                interface I {
                    fun stuff() {}
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should flag the nested empty function") {
            val code = """
                fun a() {
                    fun b() {}
                }"""
            assertThat(subject.compileAndLint(code)).hasSourceLocation(2, 13)
        }

        context("some overridden functions") {

            val code = """
                fun empty() {}

                open class Base {
                    open fun stuff() {}
                }

                class A : Base() {
                    override fun stuff() {}
                }

                class B : Base() {
                    override fun stuff() {
                        TODO("Implement this")
                    }
                }

                class C : Base() {
                    override fun stuff() {
                        // this is necessary...
                    }
                }"""

            it("should flag empty block in overridden function") {
                assertThat(subject.compileAndLint(code)).hasSize(2)
            }

            it("should not flag overridden functions") {
                val config = TestConfig(mapOf(EmptyFunctionBlock.IGNORE_OVERRIDDEN_FUNCTIONS to "true"))
                assertThat(EmptyFunctionBlock(config).compileAndLint(code)).hasSourceLocation(1, 13)
            }
        }

        context("some overridden functions") {
            val code = """
                private interface Listener {
                    fun listenThis()

                    fun listenThat()
                }

                private class AnimationEndListener : Listener {
                    override fun listenThis() {
                        // no-op
                    }

                    override fun listenThat() {

                    }
                }
            """
            it("should not flag overridden functions with commented body") {
                assertThat(subject.compileAndLint(code)).hasSourceLocation(12, 31)
            }

            it("should not flag overridden functions with ignoreOverridden") {
                val config = TestConfig(mapOf(EmptyFunctionBlock.IGNORE_OVERRIDDEN to "true"))
                assertThat(EmptyFunctionBlock(config).compileAndLint(code)).isEmpty()
            }
        }
    }
})
