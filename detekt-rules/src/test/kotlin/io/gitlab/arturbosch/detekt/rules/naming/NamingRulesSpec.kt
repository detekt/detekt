package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class NamingRulesSpec : SubjectSpek<NamingRules>({

	subject { NamingRules() }

	describe("properties in classes") {

		it("should detect all positive cases") {
			val code = """
				class C {
					private val _FIELD = 5
					val FIELD get() = _field
					val camel_Case_Property = 5
					const val MY_CONST = 7
					const val MYCONST = 7
				}
			"""
			val findings = subject.lint(code)
			assertThat(findings).hasSize(5)
		}

		it("checks all negative cases") {
			val code = """
				class C {
					private val _field = 5
					val field get() = _field
					val camelCaseProperty = 5
					const val myConst = 7

					data class D(val i: Int, val j: Int)
					fun doStuff() {
						val (_, holyGrail) = D(5, 4)
						emptyMap<String, String>().forEach { _, v -> println(v) }
					}
					val doable: (Int) -> Unit = { _ -> Unit }
				}
			"""
			val findings = subject.lint(code)
			assertThat(findings).isEmpty()
		}
	}

	describe("naming like in constants is allowed for destructuring and lambdas") {
		it("should not detect any") {
			val code = """
				data class D(val i: Int, val j: Int)
				fun doStuff() {
					val (_, HOLY_GRAIL) = D(5, 4)
					emptyMap<String, String>().forEach { _, V -> println(v) }
				}
			"""
			val findings = subject.lint(code)
			assertThat(findings).isEmpty()
		}
	}
})
