package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class ModifierOrderSpec : Spek({

	given("a kt class with wrongly ordered modifiers") {
		val file = "data internal class Test(val test: String)"

		it("should report modifiers") {
			val rule = ModifierOrder()

			val findings = rule.lint(file)
			Assertions.assertThat(findings).hasSize(1)
		}

		it("should not report issues if inactive") {
			val rule = ModifierOrder(TestConfig(mapOf("active" to "false")))

			val findings = rule.lint(file)
			Assertions.assertThat(findings).isEmpty()
		}
	}

	given("a kt class with correctly ordered modifiers") {
		val file = "internal data class Test(val test: String)"

		it("should not report modifiers") {
			val rule = ModifierOrder()

			val findings = rule.lint(file)
			Assertions.assertThat(findings).isEmpty()
		}
	}

	given("a kt parameter with wrongly ordered modifiers") {
		val code = "lateinit internal private val test: String"

		it("should report modifiers") {
			val rule = ModifierOrder()

			val findings = rule.lint(code)
			Assertions.assertThat(findings).hasSize(1)
		}
	}

	given("a kt parameter with correctly ordered modifiers") {
		val code = "private internal lateinit val test: String"

		it("should not report modifiers") {
			val rule = ModifierOrder()

			val findings = rule.lint(code)
			Assertions.assertThat(findings).isEmpty()
		}
	}

	given("a kt file with correctly ordered modifiers") {
		it("should not report correct modifiers") {
			val rule = ModifierOrder()

			val findings = rule.lint(Case.Default.path())
			Assertions.assertThat(findings).isEmpty()
		}
	}

	given("a kt file with incorrectly ordered modifiers") {
		it("should report incorrect modifiers") {
			val rule = ModifierOrder()

			val findings = rule.lint(Case.ModifierOrder.path())
			Assertions.assertThat(findings).hasSize(3)
		}
	}

})
