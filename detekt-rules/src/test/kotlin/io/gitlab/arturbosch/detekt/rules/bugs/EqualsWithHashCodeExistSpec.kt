package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class EqualsWithHashCodeExistSpec : SubjectSpek<EqualsWithHashCodeExist>({
	subject { EqualsWithHashCodeExist(Config.empty) }

	given("some classes with equals() and hashCode() functions") {

		it("reports hashCode() without equals() function") {
			val code = """
				class A {
					override fun hashCode(): Int { return super.hashCode() }
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports equals() without hashCode() function") {
			val code = """
				class A {
					override fun equals(other: Any?): Boolean { return super.equals(other) }
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not report equals() with hashCode() function") {
			val code = """
				class A {
					override fun equals(other: Any?): Boolean { return super.equals(other) }
					override fun hashCode(): Int { return super.hashCode() }
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}

	given("a data class") {

		it("does not report equals() or hashcode() violation") {
			val code = """
				data class EqualsData(val i: Int) {
					override fun equals(other: Any?): Boolean {
						return super.equals(other)
					}
				}"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})
