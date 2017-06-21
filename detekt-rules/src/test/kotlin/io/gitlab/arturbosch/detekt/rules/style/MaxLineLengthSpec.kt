package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class MaxLineLengthSpec : Spek({

	val file = compileForTest(Case.MaxLineLength.path()).text

	given("a kt file with some long lines") {
		it("should report no errors when maxLineLength is set to 200") {
			val rule = MaxLineLength(TestConfig(mapOf("maxLineLength" to "200")))

			val findings = rule.lint(file)
			Assertions.assertThat(findings).isEmpty()
		}

		it("should report all errors with default maxLineLength") {
			val rule = MaxLineLength()

			val findings = rule.lint(file)
			Assertions.assertThat(findings).hasSize(3)
		}
	}
})
