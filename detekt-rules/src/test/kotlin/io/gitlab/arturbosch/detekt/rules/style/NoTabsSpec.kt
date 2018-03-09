package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class NoTabsSpec : Spek({

	given("a line that contains a tab") {
		it("should flag it") {
			val rule = NoTabs()
			rule.visit(Case.NoTabsPositive.getKtFileContent())
			assertThat(rule.findings).hasSize(3)
		}
	}

	given("a line that does not contain a tab") {
		it("should not flag it") {
			val rule = NoTabs()
			rule.visit(Case.NoTabsNegative.getKtFileContent())
			assertThat(rule.findings).hasSize(0)
		}
	}
})
