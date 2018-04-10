package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ObjectPropertyNamingSpec : SubjectSpek<ObjectPropertyNaming>({

	subject { ObjectPropertyNaming() }

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
})
