package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class LateinitUsageSpec : Spek({

	given("a kt file with lateinit usages") {
		val code = """
			class Test {
				lateinit var first: String
				lateinit var second: Int
			}
		"""

		it("should report lateinit usages") {
			val findings = LateinitUsage().lint(code)
			Assertions.assertThat(findings).hasSize(2)
		}
	}
})
