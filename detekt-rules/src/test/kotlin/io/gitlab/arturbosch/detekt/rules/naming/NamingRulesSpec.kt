package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TEST_FILENAME
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class NamingRulesSpec : SubjectSpek<NamingRules>({

	val fileName = TEST_FILENAME

	subject { NamingRules() }

	describe("properties in classes") {

		it("should detect all positive cases") {
			val code = """
				class C(val CONST_PARAMETER: String, private val PRIVATE_CONST_PARAMETER: Int) {
					private val _FIELD = 5
					val FIELD get() = _field
					val camel_Case_Property = 5
					const val MY_CONST = 7
					const val MYCONST = 7
					fun doStuff(FUN_PARAMETER: String) {}
				}
			"""
			assertThat(subject.lint(code)).hasLocationStrings(
					"'private val _FIELD = 5' at (2,2) in /$fileName",
					"'val FIELD get() = _field' at (3,2) in /$fileName",
					"'val camel_Case_Property = 5' at (4,2) in /$fileName",
					"'const val MY_CONST = 7' at (5,2) in /$fileName",
					"'const val MYCONST = 7' at (6,2) in /$fileName",
					"'val CONST_PARAMETER: String' at (1,9) in /$fileName",
					"'private val PRIVATE_CONST_PARAMETER: Int' at (1,38) in /$fileName",
					"'FUN_PARAMETER: String' at (7,14) in /$fileName"
			)
		}

		it("checks all negative cases") {
			val code = """
				class C(val constParameter: String, private val privateConstParameter: Int) {
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
					fun doStuff(funParameter: String) {}
				}
			"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("should not flag overridden member properties by default") {
			val code = """
				class C {
					override val SHOULD_NOT_BE_FLAGGED = "banana"
				}
				interface I {
					override val SHOULD_NOT_BE_FLAGGED_2 = "banana"
				}
			"""
			assertThat(NamingRules().lint(code)).isEmpty()
		}

		it("doesn't ignore overridden member properties if ignoreOverridden is false") {
			val code = """
				class C {
					override val SHOULD_BE_FLAGGED = "banana"
				}
				interface I {
					override val SHOULD_BE_FLAGGED_2 = "banana"
				}
			"""
			val config = TestConfig(mapOf("ignoreOverridden" to "false"))
			assertThat(NamingRules(config).lint(code)).hasLocationStrings(
					"'override val SHOULD_BE_FLAGGED = \"banana\"' at (2,2) in /$fileName",
					"'override val SHOULD_BE_FLAGGED_2 = \"banana\"' at (5,2) in /$fileName"
			)
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
			assertThat(subject.lint(code)).isEmpty()
		}
	}
})
