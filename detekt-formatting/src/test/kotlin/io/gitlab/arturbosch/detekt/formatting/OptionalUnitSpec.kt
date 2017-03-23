package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class OptionalUnitSpec : Spek({
	describe("running specified rule") {
		it("should detect one finding") {
			val findings = OptionalUnit().lint("""
				fun returnsUnit(): Unit {
				}
			""")
			assertThat(findings).hasSize(1)
		}

		it("should delete Unit return type") {
			val actual = OptionalUnit().format("""
				fun returnsUnit(): Unit {
				}
			""")
			val expected = """
				fun returnsUnit() {
				}
			""".trimIndent()
			assertThat(actual).isEqualTo(expected)
		}
	}
})