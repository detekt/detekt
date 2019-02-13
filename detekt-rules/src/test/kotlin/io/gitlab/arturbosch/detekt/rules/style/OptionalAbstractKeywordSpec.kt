package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class OptionalAbstractKeywordSpec : Spek({
    val subject by memoized { OptionalAbstractKeyword() }

    describe("some abstract keyword definitions are checked for optionality") {

        it("does not report abstract keywords on an interface") {
            val code = "interface A {}"
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("reports abstract interface with abstract property") {
            val code = "abstract interface A { abstract var x: Int }"
            assertThat(subject.lint(code)).hasSize(2)
        }

        it("reports abstract interface with abstract function") {
            val code = "abstract interface A { abstract fun x() }"
            assertThat(subject.lint(code)).hasSize(2)
        }

        it("reports nested abstract interface") {
            val code = """
				class A {
					abstract interface B {
						abstract fun x()
					}
				}"""
            assertThat(subject.lint(code)).hasSize(2)
        }

        it("does not report an abstract class") {
            val code = "abstract class A { abstract fun x() }"
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("does not report a nested abstract class function") {
            val code = """@Subcomponent
				interface I {
				    abstract class A {
						abstract fun dependency
				    }
				}"""
            assertThat(subject.lint(code)).hasSize(0)
        }
    }
})
