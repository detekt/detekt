package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class OptionalUnitSpec : SubjectSpek<OptionalUnit>({
	subject { OptionalUnit(Config.empty) }

	describe("running specified rule") {

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

		it("should not report Unit reference") {
			val findings = subject.lint("""
				fun returnsNothing() {
					Unit
				}
			""")
			assertThat(findings).isEmpty()
		}

		it("should not report Unit return type in overridden function") {
			val findings = subject.lint("""
				override fun returnsUnit2() = Unit
			""")
			assertThat(findings).isEmpty()
		}
	}
})
