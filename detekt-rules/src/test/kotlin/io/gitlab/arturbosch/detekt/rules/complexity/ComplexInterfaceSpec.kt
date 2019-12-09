package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ComplexInterfaceSpec : Spek({

    val subject by memoized { ComplexInterface(threshold = THRESHOLD) }
    val config = TestConfig(mapOf(ComplexInterface.INCLUDE_STATIC_DECLARATIONS to "true"))

    describe("ComplexInterface rule positives") {

        context("interface members") {
            val code = """
                interface I {
                    fun f1()
                    fun f2()
                    val i1: Int
                    fun fImpl() {}
                }
            """

            it("reports complex interface") {
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports complex interface with includeStaticDeclarations config") {
                val rule = ComplexInterface(config, threshold = THRESHOLD)
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }

        context("nested interface members") {
            val code = """
                class I {
                    interface Nested {
                        fun f1()
                        fun f2()
                        val i1: Int
                        fun fImpl() {}
                    }
                }
            """

            it("reports complex interface") {
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports complex interface with includeStaticDeclarations config") {
                val rule = ComplexInterface(config, threshold = THRESHOLD)
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }

        context("interface with static declarations") {
            val code = """
                interface I {
                    fun f1()
                    companion object {
                        fun sf() = 0
                        const val c = 0
                        val v = 0
                    }
                }
            """

            it("does not report static declarations per default") {
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports complex interface with includeStaticDeclarations config") {
                val rule = ComplexInterface(config, threshold = THRESHOLD)
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }
    }

    describe("ComplexInterface rule negatives") {

        it("does not report a simple interface ") {
            val code = """
                interface I {
                    fun f()
                    fun fImpl() {
                        val x = 0 // should not report
                    }
 
                    val i: Int
                    // a comment shouldn't be detected
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a simple interface with a companion object") {
            val code = """
                interface I {
                    fun f()

                    companion object {
                        fun sf() = 0
                        const val c = 0
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report an empty interface") {
            val code = "interface Empty"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})

private const val THRESHOLD = 4
