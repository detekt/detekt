package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class SubRuleSpec : Spek({

	val file = compileForTest(Case.Default.path())

	given("a SubRule that always reports") {
		val rule = object: SubRule<String>(TestConfig(emptyMap())) {
			override fun apply(element: String) {
				report(CodeSmell(issue, Entity.from(file)))
			}

			override val issue = Issue("Test", Severity.Style, "Test")
		}

		it("should report an issue") {
			rule.verify("Test") {
				Assertions.assertThat(rule.visitCondition(file)).isTrue()
				Assertions.assertThat(it).hasSize(1)
			}
		}
	}

	given("a SubRule that always reports but is inactive") {
		val rule = object: SubRule<String>(TestConfig(mapOf("active" to "false"))) {
			override fun apply(element: String) {
				report(CodeSmell(issue, Entity.from(file)))
			}

			override val issue = Issue("Test", Severity.Style, "Test")
		}

		it("should not be active") {
			Assertions.assertThat(rule.visitCondition(file)).isFalse()
		}
	}
})
