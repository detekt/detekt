package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseCheckOrErrorSpec : Spek({

    val subject by memoized { UseCheckOrError(Config.empty) }

    describe("UseCheckOrError rule") {

        it("reports if a an IllegalStateException is thrown") {
            val code = """
				fun x() {
					doSomething()
                    if (a < 0) throw IllegalStateException()
				}"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if a an IllegalStateException is thrown with an error message") {
            val code = """
				fun x() {
					doSomething()
                    if (a < 0) throw IllegalStateException("More details")
				}"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if a an IllegalStateException is thrown as default case of a when expression") {
            val code = """
                fun x(a: Int) =
                    when (a) {
                        1 -> doSomething()
                        else -> throw IllegalStateException()
                    }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if an IllegalStateException is thrown by its fully qualified name") {
            val code = """
				fun x() {
					doSomething()
                    if (a < 0) throw java.lang.IllegalStateException()
				}"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports if an IllegalStateException is thrown by its fully qualified name using the kotlin type alias") {
            val code = """
				fun x() {
					doSomething()
                    if (a < 0) throw kotlin.IllegalStateException()
				}"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report if any other kind of exception is thrown") {
            val code = """
				fun x() {
					doSomething()
                    if (a < 0) throw SomeBusinessException()
				}"""
            assertThat(subject.lint(code)).isEmpty()
        }
    }
})
