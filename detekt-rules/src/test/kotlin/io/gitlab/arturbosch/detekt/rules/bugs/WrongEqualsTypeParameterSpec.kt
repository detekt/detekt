package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class WrongEqualsTypeParameterSpec : Spek({
    val subject by memoized { WrongEqualsTypeParameter(Config.empty) }

    describe("WrongEqualsTypeParameter rule") {

        it("uses Any? as parameter") {
            val code = """
				class A {
					override fun equals(other: Any?): Boolean {
						return super.equals(other)
					}
				}"""
            assertThat(subject.lint(code).size).isEqualTo(0)
        }

        it("uses String as parameter") {
            val code = """
				class A {
					fun equals(other: String): Boolean {
						return super.equals(other)
					}
				}"""
            assertThat(subject.lint(code).size).isEqualTo(1)
        }

        it("uses an interface declaration") {
            val code = """
				interface EqualsInterf {
					fun equals(other: String)
				}"""
            assertThat(subject.lint(code).size).isEqualTo(0)
        }
    }
})
