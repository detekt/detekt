package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Context
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

	it("all findings are suppressed on element levels") {
		val ktFile = compileForTest(Case.SuppressedElements.path())
		val ruleSet = RuleSet("Test", listOf(LongMethod(), LongParameterList(), ComplexCondition()))
		val context = Context()
		ruleSet.accept(context, ktFile)
		val findings = context.findings.flatMap { it.value }
		findings.forEach {
			println(it.compact())
		}
		assertThat(findings).hasSize(0)
	}

	it("all findings are suppressed on file levels") {
		val ktFile = compileForTest(Case.SuppressedElementsByFile.path())
		val ruleSet = RuleSet("Test", listOf(LongMethod(), LongParameterList(), ComplexCondition()))
		val context = Context()
		ruleSet.accept(context, ktFile)
		val findings = context.findings.flatMap { it.value }
		findings.forEach {
			println(it.compact())
		}
		assertThat(findings).hasSize(0)
	}

	it("all findings are suppressed on class levels") {
		val ktFile = compileForTest(Case.SuppressedElementsByClass.path())
		val ruleSet = RuleSet("Test", listOf(LongMethod(), LongParameterList(), ComplexCondition()))
		val context = Context()
		ruleSet.accept(context, ktFile)
		val findings = context.findings.flatMap { it.value }
		findings.forEach {
			println(it.compact())
		}
		assertThat(findings).hasSize(0)
	}

})