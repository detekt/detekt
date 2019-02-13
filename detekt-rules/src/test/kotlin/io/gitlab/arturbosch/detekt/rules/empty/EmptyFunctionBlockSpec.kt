package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class EmptyFunctionBlockSpec : Spek({

    val subject by memoized { EmptyFunctionBlock(Config.empty) }

    describe("EmptyFunctionBlock rule") {

        it("should flag function with protected modifier") {
            val code = "protected fun stuff() {}"
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("should not flag function with open modifier") {
            val code = "open fun stuff() {}"
            assertThat(subject.lint(code)).isEmpty()
        }

        it("should flag the nested empty function") {
            val code = """
				fun a() {
					fun b() {}
				}"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        context("some overridden functions") {

            val code = """
					fun empty() {}

					override fun stuff1() {}

					override fun stuff2() {
						TODO("Implement this")
					}

					override fun stuff3() {
						// this is necessary...
					}"""

            it("should flag empty block in overridden function") {
                assertThat(subject.lint(code)).hasSize(2)
            }

            it("should not flag overridden functions") {
                val config = TestConfig(mapOf(EmptyFunctionBlock.IGNORE_OVERRIDDEN_FUNCTIONS to "true"))
                assertThat(EmptyFunctionBlock(config).lint(code)).hasSize(1)
            }
        }
    }
})
