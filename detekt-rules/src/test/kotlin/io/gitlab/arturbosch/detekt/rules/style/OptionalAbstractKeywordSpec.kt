package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author schalkms
 */
class OptionalAbstractKeywordSpec : Spek({
    val subject by memoized { OptionalAbstractKeyword() }

    describe("some abstract keyword definitions are checked for optionality") {

        it("does not report abstract keywords on an interface") {
            val code = "interface A {}"
            assertThat(subject.compileAndLint(code)).hasSize(0)
        }

        it("reports abstract interface with abstract property") {
            val code = "abstract interface A { abstract var x: Int }"
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("reports abstract interface with abstract function") {
            val code = "abstract interface A { abstract fun x() }"
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("reports nested abstract interface") {
            val code = """
				class A {
					abstract interface B {
						abstract fun x()
					}
				}"""
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("does not report an abstract class") {
            val code = "abstract class A { abstract fun x() }"
            assertThat(subject.compileAndLint(code)).hasSize(0)
        }

        it("does not report a nested abstract class function") {
            val code = """
                interface I {
                    abstract class A {
						abstract fun dependency()
				    }
				}"""
            assertThat(subject.compileAndLint(code)).hasSize(0)
        }
    }
})
