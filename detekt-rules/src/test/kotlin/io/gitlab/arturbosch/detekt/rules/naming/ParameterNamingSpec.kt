package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Mickele Moriconi
 * @author Paweł Gajda
 */
class ParameterNamingSpec : Spek({

	describe("parameters in a constructor of a class") {

		it("should detect no violations") {
			val code = """
				class C(val param: String, private val privateParam: String)

				class C {
					construct(val param: String) {}
					construct(val param: String, private val privateParam: String) {}
				}
			"""
			assertThat(ConstructorParameterNaming().lint(code)).isEmpty()
		}

		it("should find some violations") {
			val code = """
				class C(val PARAM: String, private val PRIVATE_PARAM: String)

				class C {
					construct(val PARAM: String) {}
					construct(val PARAM: String, private val PRIVATE_PARAM: String) {}
				}
			"""
			assertThat(NamingRules().lint(code)).hasSize(5)
		}
	}

	describe("parameters in a function of a class") {

		it("should detect no violations") {
			val code = """
				class C {
					fun someStuff(param: String) {}
				}
			"""
			assertThat(ConstructorParameterNaming().lint(code)).isEmpty()
		}

		it("should not detect violations in overridden function by default") {
			val code = """
				class C {
					override fun someStuff(`object`: String) {}
				}
			"""
			assertThat(FunctionParameterNaming().lint(code)).isEmpty()
		}

		it("should not detect violations in underscore parameter name by default") {
			val code = """
				class C {
					override fun someStuff(_: String) {}
				}
			"""
			assertThat(FunctionParameterNaming().lint(code)).isEmpty()
		}

		it("should detect violations in overridden function if ignoreOverriddenFunctions is false") {
			val code = """
				class C {
					override fun someStuff(`object`: String) {}
				}
			"""
			val config = TestConfig(mapOf("ignoreOverriddenFunctions" to "false"))
			assertThat(FunctionParameterNaming(config).lint(code)).hasSize(1)
		}

		it("should find some violations") {
			val code = """
				class C {
					fun someStuff(PARAM: String) {}
				}
			"""
			assertThat(NamingRules().lint(code)).hasSize(1)
		}
	}

	describe("parameters in a function of an excluded class") {

		val config = TestConfig(mapOf("excludeClassPattern" to "Excluded"))

		it("should not detect function parameter") {
			val code = """
				class Excluded {
					fun f(PARAM: Int)
				}
			"""
			assertThat(FunctionParameterNaming(config).lint(code)).isEmpty()
		}

		it("should not detect constructor parameter") {
			val code = "class Excluded(val PARAM: Int) {}"
			assertThat(ConstructorParameterNaming(config).lint(code)).isEmpty()
		}
	}
})
