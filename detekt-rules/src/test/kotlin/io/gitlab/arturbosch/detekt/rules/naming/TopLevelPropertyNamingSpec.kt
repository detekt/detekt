package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class TopLevelPropertyNamingSpec : SubjectSpek<NamingRules>({

	subject { NamingRules() }

	describe("constants on top level") {
		val code = compileContentForTest("""
			const val MY_NAME_8 = "Artur"
            const val MYNAME = "Artur"
            const val MyNAME = "Artur" // invalid
			const val name = "Artur" // invalid
			const val nAme = "Artur" // invalid
			private const val _nAme = "Artur" // invalid
            const val serialVersionUID = 42L // invalid
		""")

		it("should detect five constants not matching [A-Z][_A-Z\\d]*") {
			val findings = TopLevelPropertyNaming().lint(code)
			assertThat(findings).hasSize(5)
		}
	}

	describe("variables on top level") {
		val code = compileContentForTest("""
			val MY_NAME = "Artur" // invalid
            val MYNAME = "Artur" // invalid
            val MyNAME = "Artur" // invalid
			val name = "Artur"
			val nAme8 = "Artur"
			val _nAme = "Artur" // invalid
			private val _name = "Artur"
			private val NAME = "Artur // invalid
            val serialVersionUID = 42L
		""")

		val findings = subject.lint(code)

		it("should detect four top level variables not matching [a-z][A-Za-z\\d]*") {
			assertThat(findings).hasSize(5)
		}

		it("should allow underscores in private property") {
			assertThat(findings.find { it.name == "_name" }).isNull()
		}
	}

	describe("constants in object declarations") {
		val code = compileContentForTest("""
			object O {
				const val MY_NAME_8 = "Artur"
				const val MYNAME = "Artur"
				const val MyNAME = "Artur"
				const val name = "Artur"
				const val nAme = "Artur"
				const val _nAme = "Artur" // invalid
				const val serialVersionUID = 42L
			}
		""")

		it("should detect one constant not matching [A-Za-z][_A-Za-z\\d]*") {
			val findings = subject.lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	describe("constants in companion object") {
		val code = compileContentForTest("""
			class C {
				companion object {
					const val MY_NAME_8 = "Artur"
					const val MYNAME = "Artur"
					const val MyNAME = "Artur"
					const val name = "Artur"
					const val nAme = "Artur"
					const val _nAme = "Artur" // invalid
					const val serialVersionUID = 42L
				}
			}
		""")

		it("should detect one constant not matching [A-Za-z][_A-Za-z\\d]*") {
			val findings = subject.lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	describe("variables in objects") {
		val code = compileContentForTest("""
			object O {
				val MY_NAME = "Artur"
				val MYNAME = "Artur"
				val MyNAME = "Artur"
				val name = "Artur"
				val nAme8 = "Artur"
				val _nAme = "Artur" // invalid
				private val _name = "Artur" // invalid
				val serialVersionUID = 42L
			}
		""")

		it("should detect two object properties not matching [A-Za-z][_A-Za-z\\d]*") {
			val findings = subject.lint(code)
			assertThat(findings).hasSize(2)
		}
	}

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
