package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class EqualsWithHashCodeExistSpec : Spek({
    val subject by memoized { EqualsWithHashCodeExist(Config.empty) }

    describe("Equals With Hash Code Exist rule") {

        context("some classes with equals() and hashCode() functions") {

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
                assertThat(subject.lint(code)).isEmpty()
            }

            it("does not report when using kotlin.Any?") {
                val code = """
				class A {
					override fun equals(other: kotlin.Any?): Boolean { return super.equals(other) }
					override fun hashCode(): Int { return super.hashCode() }
				}"""
                assertThat(subject.lint(code)).isEmpty()
            }
        }

        context("a data class") {

            it("does not report equals() or hashcode() violation on data class") {
                val code = """
				data class EqualsData(val i: Int) {
					override fun equals(other: Any?): Boolean {
						return super.equals(other)
					}
				}"""
                assertThat(subject.lint(code)).hasSize(0)
            }
        }
    }
})
