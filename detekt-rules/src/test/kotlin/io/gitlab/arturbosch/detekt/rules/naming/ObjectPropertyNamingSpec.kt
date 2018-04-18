package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ObjectPropertyNamingSpec : SubjectSpek<ObjectPropertyNaming>({

	subject { ObjectPropertyNaming() }

	val negative = """
					const val MY_NAME_8 = "Artur"
					const val MYNAME = "Artur"
					const val MyNAME = "Artur"
					const val name = "Artur"
					const val nAme = "Artur"
					const val serialVersionUID = 42L"""
	val positive = """const val _nAme = "Artur""""

	describe("constants in object declarations") {

		it("should not detect any constants not complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					$negative
				}
			""")
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("should detect constants not complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					$positive
				}
			""")
			assertThat(subject.lint(code)).hasSize(1)
		}
	}

	describe("constants in companion object") {

		it("should not detect any constants not complying to the naming rules") {
			val code = compileContentForTest("""
				class C {
					companion object {
						$negative
					}
				}
			""")
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("should detect constants not complying to the naming rules") {
			val code = compileContentForTest("""
				class C {
					companion object {
						$positive
					}
				}
			""")
			assertThat(subject.lint(code)).hasSize(1)
		}
	}

	describe("variables in objects") {

		it("should not detect any constants not complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					val MY_NAME = "Artur"
					val MYNAME = "Artur"
					val MyNAME = "Artur"
					val name = "Artur"
					val nAme8 = "Artur"
					val serialVersionUID = 42L
				}
			""")
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("should detect constants not complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					val _nAme = "Artur"
					private val _name = "Artur"
				}
			""")
			assertThat(subject.lint(code)).hasSize(2)
		}
	}

	describe("variables and constants in objects with custom config") {

		val config = TestConfig(mapOf(ObjectPropertyNaming.CONSTANT_PATTERN to "_[A-Za-z]*"))
		val subject = ObjectPropertyNaming(config)

		it("should not detect constants in object with underscores") {
			val code = compileContentForTest("""
				object O {
					const val _NAME = "Artur"
					const val _name = "Artur"
				}
			""")
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})
