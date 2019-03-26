package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author schalkms
 */
class WrongEqualsTypeParameterSpec : Spek({
    val subject by memoized { WrongEqualsTypeParameter(Config.empty) }

    describe("WrongEqualsTypeParameter rule") {

        it("does not report Any? as parameter") {
            val code = """
				class A {
					override fun equals(other: Any?): Boolean {
						return super.equals(other)
					}
				}"""
            assertThat(subject.compileAndLint(code).size).isEqualTo(0)
        }

        it("reports a String as parameter") {
            val code = """
				class A {
					fun equals(other: String): Boolean {
						return super.equals(other)
					}
				}"""
            assertThat(subject.compileAndLint(code).size).isEqualTo(1)
        }

        it("does not report an interface declaration") {
            val code = """
				interface I {
					fun equals(other: String)
				}"""
            assertThat(subject.compileAndLint(code).size).isEqualTo(0)
        }
    }
})
