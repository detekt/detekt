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

		it("should detect one finding") {
			val findings = subject.lint("""
				fun returnsUnit1(): Unit {
					fun returnsUnitNested(): Unit {
						return Unit
					}
					return Unit
				}

				fun returnsUnit2() = Unit
			""")
			assertThat(findings).hasSize(3)
		}

		it("should not report Unit return type in overridden function") {
			val findings = subject.lint("""
				override fun returnsUnit2() = Unit
			""")
			assertThat(findings).isEmpty()
		}
	}

	given("several Unit references") {

		it("should report lone Unit statement") {
			val findings = subject.lint("""
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
			""")
			assertThat(findings).hasSize(4)
		}

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
