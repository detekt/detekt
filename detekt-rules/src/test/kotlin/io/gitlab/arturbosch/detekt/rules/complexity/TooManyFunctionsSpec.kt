package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class TooManyFunctionsSpec : Spek({

	describe("a simple test") {

		val rule = TooManyFunctions()

		it("should find one file with too many functions") {
			assertThat(rule.lint(Case.TooManyFunctions.path())).hasSize(1)
		}

		it("should find one file with too many top level functions") {
			assertThat(rule.lint(Case.TooManyFunctionsTopLevel.path())).hasSize(1)
		}
	}

	describe("different declarations with one function as threshold") {

		val rule = TooManyFunctions(TestConfig(mapOf(
				TooManyFunctions.THRESHOLD_IN_CLASSES to "1",
				TooManyFunctions.THRESHOLD_IN_ENUMS to "1",
				TooManyFunctions.THRESHOLD_IN_FILES to "1",
				TooManyFunctions.THRESHOLD_IN_INTERFACES to "1",
				TooManyFunctions.THRESHOLD_IN_OBJECTS to "1"
		)))

		it("finds one function in class") {
			val code = """
				class A {
					fun a() = Unit
				}
			"""

			assertThat(rule.lint(code)).hasSize(1)
		}

		it("finds one function in object") {
			val code = """
				object O {
					fun o() = Unit
				}
			"""

			assertThat(rule.lint(code)).hasSize(1)
		}

		it("finds one function in interface") {
			val code = """
				interface I {
					fun i()
				}
			"""

			assertThat(rule.lint(code)).hasSize(1)
		}

		it("finds one function in enum") {
			val code = """
				enum class E {
					fun E()
				}
			"""

			assertThat(rule.lint(code)).hasSize(1)
		}

		it("finds one function in file") {
			val code = "fun f = Unit"

			assertThat(rule.lint(code)).hasSize(1)
		}

		it("finds one function in file ignoring other declarations") {
			val code = """
				fun f1 = Unit
				class C
				object O
				fun f2 = Unit
				interface I
				enum class E
				fun f3 = Unit
			"""

			assertThat(rule.lint(code)).hasSize(1)
		}

		it("finds one function in nested class") {
			val code = """
				class A {
					class B {
						class C {
							fun a() = Unit
						}
					}
				}
			"""

			assertThat(rule.lint(code)).hasSize(1)
		}
	}
})
