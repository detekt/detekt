package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ModifierOrderSpec : SubjectSpek<ModifierOrder>({
	subject { ModifierOrder(Config.empty) }

	given("kt classes with modifiers") {
		val bad1 = "data internal class Test(val test: String)"
		val bad2 = "actual private class Test(val test: String)"
		val bad3 = "annotation expect class Test"

		it("should report incorrectly ordered modifiers") {
			assertThat(subject.lint(bad1)).hasSize(1)
			assertThat(subject.lint(bad2)).hasSize(1)
			assertThat(subject.lint(bad3)).hasSize(1)
		}

		it("does not report correctly ordered modifiers") {
			assertThat(subject.lint("internal data class Test")).isEmpty()
			assertThat(subject.lint("private actual class Test(val test: String)")).isEmpty()
			assertThat(subject.lint("expect annotation class Test")).isEmpty()
		}

		it("should not report issues if inactive") {
			val rule = ModifierOrder(TestConfig(mapOf("active" to "false")))
			assertThat(rule.lint(bad1)).isEmpty()
			assertThat(rule.lint(bad2)).isEmpty()
			assertThat(rule.lint(bad3)).isEmpty()
		}
	}

	given("a kt parameter with modifiers") {

		it("should report wrongly ordered modifiers") {
			val code = "lateinit internal private val test: String"
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not report correctly ordered modifiers") {
			val code = "private internal lateinit val test: String"
			assertThat(subject.lint(code)).isEmpty()
		}

	}

	given("an overridden function") {

		it("should report incorrectly ordered modifiers") {
			val code = """
				abstract class Test {
					override open fun test() {}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not report correctly ordered modifiers") {
			val code = """
				abstract class Test {
					override fun test() {}
				}"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	given("a tailrec function") {

		it("should report incorrectly ordered modifiers") {
			val code = "tailrec private fun findFixPoint(x: Double = 1.0): Double"
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not report correctly ordered modifiers") {
			val code = "private tailrec fun findFixPoint(x: Double = 1.0): Double"
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	given("a vararg argument") {

		it("should report incorrectly ordered modifiers") {
			val code = "fun foo(vararg private val strings: String) {}"
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not report correctly ordered modifiers") {
			val code = "fun foo(private vararg val strings: String) {}"
			assertThat(subject.lint(code)).isEmpty()
		}
	}
	
})
