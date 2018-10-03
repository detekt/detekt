package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Mickele Moriconi
 */
class ParameterNamingSpec : Spek({

	describe("parameters in a constructor of a class") {

		it("should detect no violations") {
			val findings = ConstructorParameterNaming().lint(
					"""
					class C(val param: String, private val privateParam: String)

					class C {
						construct(val param: String) {}
						construct(val param: String, private val privateParam: String) {}
					}
				"""
			)
			assertThat(findings).isEmpty()
		}

		it("should find some violations") {
			val findings = NamingRules().lint(
					"""
					class C(val PARAM: String, private val PRIVATE_PARAM: String)

					class C {
						construct(val PARAM: String) {}
						construct(val PARAM: String, private val PRIVATE_PARAM: String) {}
					}
				"""
			)
			assertThat(findings).hasSize(5)
		}
	}

	describe("parameters in a function of a class") {

		it("should detect no violations") {
			val findings = ConstructorParameterNaming().lint(
					"""
					class C {
						fun someStuff(param: String) {}
					}
				"""
			)
			assertThat(findings).isEmpty()
		}

		it("should not detect violations in overridden function by default") {
			val findings = FunctionParameterNaming().lint(
					"""
					class C {
						override fun someStuff(`object`: String) {}
					}
				"""
			)
			assertThat(findings).isEmpty()
		}

		it("should detect violations in overridden function if ignoreOverriddenFunctions is false") {
			val config = TestConfig(mapOf("ignoreOverriddenFunctions" to "false"))
			val findings = FunctionParameterNaming(config).lint(
					"""
					class C {
						override fun someStuff(`object`: String) {}
					}
				"""
			)
			assertThat(findings).hasSize(1)
		}

		it("should find some violations") {
			val findings = NamingRules().lint(
					"""
					class C {
						fun someStuff(PARAM: String) {}
					}
				"""
			)
			assertThat(findings).hasSize(1)
		}
	}
})
