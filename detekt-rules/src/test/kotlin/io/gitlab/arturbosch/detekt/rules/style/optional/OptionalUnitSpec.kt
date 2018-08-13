package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class OptionalUnitSpec : SubjectSpek<OptionalUnit>({
	subject { OptionalUnit(Config.empty) }

	given("several functions which return Unit") {

		val code = """
				fun returnsUnit1(): Unit {
					fun returnsUnitNested(): Unit {
						return Unit
					}
					return Unit
				}

				fun returnsUnit2() = Unit
			"""
		val findings = subject.lint(code)

		it("should report functions returning Unit") {
			assertThat(findings).hasSize(3)
		}

		it("should report the correct violation message") {
			findings.forEach {
				assertThat(it.message).endsWith(
						" defines a return type of Unit. This is unnecessary and can safely be removed.")
			}
		}
	}

	given("an overridden function which returns Unit") {

		it("should not report Unit return type in overridden function") {
			val code = "override fun returnsUnit2() = Unit"
			val findings = subject.lint(code)
			assertThat(findings).isEmpty()
		}
	}

	given("several lone Unit statements") {

		val code = """
				fun returnsNothing() {
					Unit
					val i: (Int) -> Unit = { _ -> Unit }
					if (true) {
						Unit
					}
				}

				class A {
					init {
						Unit
					}
				}
			"""
		val findings = subject.lint(code)

		it("should report lone Unit statement") {
			assertThat(findings).hasSize(4)
		}

		it("should report the correct violation message") {
			findings.forEach {
				assertThat(it.message).isEqualTo("A single Unit expression is unnecessary and can safely be removed")
			}
		}
	}

	given("several Unit references") {

		it("should not report Unit reference") {
			val findings = subject.lint("""
				fun returnsNothing(u: Unit, us: () -> String) {
					val u1 = u is Unit
					val u2: Unit = Unit
					val Unit = 1
					Unit.equals(null)
					val i: (Int) -> Unit = { _ -> }
				}
			""")
			assertThat(findings).isEmpty()
		}
	}
})
