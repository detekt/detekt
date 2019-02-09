package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class WrongEqualsTypeParameterSpec : SubjectSpek<WrongEqualsTypeParameter>({
    subject { WrongEqualsTypeParameter(Config.empty) }

    given("an equals method") {

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
