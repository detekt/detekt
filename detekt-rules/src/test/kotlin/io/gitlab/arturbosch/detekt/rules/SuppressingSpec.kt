package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.rules.complexity.ComplexCondition
import io.gitlab.arturbosch.detekt.rules.complexity.LongMethod
import io.gitlab.arturbosch.detekt.rules.complexity.LongParameterList
import io.gitlab.arturbosch.detekt.rules.complexity.TooManyFunctions
import io.gitlab.arturbosch.detekt.rules.style.MaxLineLength
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class SuppressingSpec : Spek({

	it("all findings are suppressed on element levels") {
		val ktFile = compileForTest(Case.SuppressedElements.path())
		val ruleSet = RuleSet("Test", listOf(LongMethod(threshold = 0),
				LongParameterList(threshold = 0),
				ComplexCondition(threshold = 0),
				MaxLineLength()))
		val findings = ruleSet.accept(ktFile)
		findings.forEach {
			println(it.compact())
		}
		assertThat(findings).hasSize(0)
	}

	it("all findings are suppressed on file levels") {
		val ktFile = compileForTest(Case.SuppressedElementsByFile.path())
		val ruleSet = RuleSet("Test", listOf(LongMethod(threshold = 0),
				LongParameterList(threshold = 0),
				ComplexCondition(threshold = 0),
				MaxLineLength()))
		val findings = ruleSet.accept(ktFile)
		findings.forEach {
			println(it.compact())
		}
		assertThat(findings).hasSize(0)
	}

	it("all findings are suppressed on class levels") {
		val ktFile = compileForTest(Case.SuppressedElementsByClass.path())
		val ruleSet = RuleSet("Test", listOf(LongMethod(threshold = 0),
				LongParameterList(threshold = 0),
				ComplexCondition(threshold = 0),
				MaxLineLength()))
		val findings = ruleSet.accept(ktFile)
		findings.forEach {
			println(it.compact())
		}
		assertThat(findings).hasSize(0)
	}

	it("should suppress TooManyFunctionsRule on class level") {
		val findings = TooManyFunctions(threshold = 0).lint(Case.SuppressedElementsByClass.path())

		assertThat(findings).isEmpty()
	}

})