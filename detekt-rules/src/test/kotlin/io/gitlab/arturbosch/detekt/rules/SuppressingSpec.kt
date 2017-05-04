package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.rules.complexity.ComplexCondition
import io.gitlab.arturbosch.detekt.rules.complexity.LongMethod
import io.gitlab.arturbosch.detekt.rules.complexity.LongParameterList
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class SuppressingSpec : Spek({

	it("all findings are suppressed on different levels") {
		val ktFile = compileForTest(Case.SuppressedElements.path())
		val ruleSet = RuleSet("Test", listOf(LongMethod(), LongParameterList(), ComplexCondition()))
		val findings = ruleSet.accept(ktFile)
		findings.forEach {
			println(it.compact())
		}
		assertThat(findings).hasSize(0)
	}

})