package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class ForEachOnRangeSpec : Spek({

	given("a kt file with using a forEach on a range") {
		val code = """
			package foo

			fun test() {
				(1..10).forEach {
					println(it)
				}
			}
		"""

		it("should report the forEach usage") {
			val findings = ForEachOnRange().lint(code)
			Assertions.assertThat(findings).hasSize(1)
		}
	}

	given("a kt file with using any other method on a range") {
		val code = """
			package foo

			fun test() {
				(1..10).isEmpty()
			}
		"""

		it("should report not report any issues") {
			val findings = ForEachOnRange().lint(code)
			Assertions.assertThat(findings).isEmpty()
		}
	}

	given("a kt file with using a forEach on a list") {
		val code = """
			package foo

			fun test() {
				listOf<Int>(1, 2, 3).forEach {
					println(it)
				}
			}
		"""

		it("should report not report any issues") {
			val findings = ForEachOnRange().lint(code)
			Assertions.assertThat(findings).isEmpty()
		}
	}
})
