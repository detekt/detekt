package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 * @author schalkms
 */
class EmptyFunctionBlockSpec : Spek({

    val subject by memoized { EmptyFunctionBlock(Config.empty) }

    describe("EmptyFunctionBlock rule") {

        it("should flag function with protected modifier") {
            val code = """
                class A {
                    protected fun stuff() {}
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should not flag function with open modifier") {
            val code = """
                open class A {
                    open fun stuff() {}
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should flag the nested empty function") {
            val code = """
				fun a() {
					fun b() {}
				}"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
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
                assertThat(EmptyFunctionBlock(config).compileAndLint(code)).hasSize(1)
            }
        }
    }
})
