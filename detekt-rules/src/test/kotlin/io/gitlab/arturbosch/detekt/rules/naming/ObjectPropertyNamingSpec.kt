package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TEST_FILENAME
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ObjectPropertyNamingSpec : SubjectSpek<ObjectPropertyNaming>({

	subject { ObjectPropertyNaming() }

	val fileName = TEST_FILENAME

	describe("constants in object declarations") {

		it("should not detect public constants complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					${PublicConst.negative}
				}
			""")
			assertThat(subject.lint(code)).isEmpty()
		}

		it("should detect public constants not complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					${PublicConst.positive}
				}
			""")
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not detect private constants complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					${PrivateConst.negative}
				}
			""")
			assertThat(subject.lint(code)).isEmpty()
		}

		it("should detect private constants not complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					${PrivateConst.positive}
				}
			""")
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should report constants not complying to the naming rules at the right position") {
			val code = compileContentForTest("""
				object O {
					${PublicConst.positive}
				}
			""")
			assertThat(subject.lint(code)).hasLocationStrings(
					"'const val _nAme = \"Artur\"' at (3,6) in /$fileName"
			)
		}
	}

	describe("constants in companion object") {

		it("should not detect public constants complying to the naming rules") {
			val code = compileContentForTest("""
				class C {
					companion object {
						${PublicConst.negative}
					}
				}
			""")
			assertThat(subject.lint(code)).isEmpty()
		}

		it("should detect public constants not complying to the naming rules") {
			val code = compileContentForTest("""
				class C {
					companion object {
						${PublicConst.positive}
					}
				}
			""")
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not detect private constants complying to the naming rules") {
			val code = compileContentForTest("""
				class C {
					companion object {
						${PrivateConst.negative}
					}
				}
			""")
			assertThat(subject.lint(code)).isEmpty()
		}

		it("should detect private constants not complying to the naming rules") {
			val code = compileContentForTest("""
				class C {
					companion object {
						${PrivateConst.positive}
					}
				}
			""")
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should report constants not complying to the naming rules at the right position") {
			val code = compileContentForTest("""
				class C {
					companion object {
						${PublicConst.positive}
					}
				}
			""")
			assertThat(subject.lint(code)).hasLocationStrings(
					"'const val _nAme = \"Artur\"' at (4,7) in /$fileName"
			)
		}
	}

	describe("variables in objects") {

		it("should not detect public constants complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					${PublicVal.negative}
				}
			""")
			assertThat(subject.lint(code)).isEmpty()
		}

		it("should detect public constants not complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					${PublicVal.positive}
				}
			""")
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not detect private constants complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					${PrivateVal.negative}
				}
			""")
			assertThat(subject.lint(code)).isEmpty()
		}

		it("should detect private constants not complying to the naming rules") {
			val code = compileContentForTest("""
				object O {
					private val __NAME = "Artur"
				}
			""")
			assertThat(subject.lint(code)).hasSize(1)
		}
	}

	describe("variables and constants in objects with custom config") {

		val config = TestConfig(mapOf(
				ObjectPropertyNaming.CONSTANT_PATTERN to "_[A-Za-z]*",
				ObjectPropertyNaming.PRIVATE_PROPERTY_PATTERN to ".*"
		))
		val subject = ObjectPropertyNaming(config)

		it("should not detect constants in object with underscores") {
			val code = compileContentForTest("""
				object O {
					const val _NAME = "Artur"
					const val _name = "Artur"
				}
			""")
			assertThat(subject.lint(code)).isEmpty()
		}

		it("should not detect private properties in object") {
			val code = compileContentForTest("""
				object O {
					private val __NAME = "Artur"
					private val _1234 = "Artur"
				}
			""")
			assertThat(subject.lint(code)).isEmpty()
		}
	}
})

abstract class NamingSnippet(private val isPrivate: Boolean, private val isConst: Boolean) {

	val negative = """
					${visibility()}${const()}val MY_NAME_8 = "Artur"
					${visibility()}${const()}val MYNAME = "Artur"
					${visibility()}${const()}val MyNAME = "Artur"
					${visibility()}${const()}val name = "Artur"
					${visibility()}${const()}val nAme = "Artur"
					${visibility()}${const()}val serialVersionUID = 42L"""
	val positive = """${visibility()}${const()}val _nAme = "Artur""""

	private fun visibility() = if (isPrivate) "private " else ""
	private fun const() = if (isConst) "const " else ""
}

object PrivateConst : NamingSnippet(true, true)
object PublicConst : NamingSnippet(false, true)
object PrivateVal : NamingSnippet(true, false)
object PublicVal : NamingSnippet(false, false)
