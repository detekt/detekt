package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
internal class MultiRuleTest : Spek({

	describe("a multi rule can have filters") {

		fun ruleSet() = RuleSet("TestMultiRule", listOf(TestMultiRule()))
		val file = compilerFor("FilteredClass.kt")

		it("should not run any rules") {
			val filters = setOf("TestRuleOne", "TestRuleTwo")
			val findings = ruleSet().accept(file, filters)

			Assertions.assertThat(findings).isEmpty()
		}

		it("should only run one rule") {
			val filters = setOf("TestRuleOne")
			val findings = ruleSet().accept(file, filters)

			Assertions.assertThat(findings).hasSize(1)

		}
	}
})

class TestMultiRule : MultiRule() {

	private val one = TestRuleOne()
	private val two = TestRuleTwo()
	override val rules: List<Rule> = listOf(one, two)

	override fun visitKtFile(file: KtFile) {
		one.runIfActive { visitKtFile(file) }
		two.runIfActive { visitKtFile(file) }
	}
}

abstract class AbstractRule : Rule() {
	override val issue: Issue = Issue(javaClass.simpleName, Severity.Minor)

	override fun visitKtFile(file: KtFile) {
		report(CodeSmell(issue, Entity.from(file)))
	}
}

class TestRuleOne : AbstractRule()
class TestRuleTwo : AbstractRule()
