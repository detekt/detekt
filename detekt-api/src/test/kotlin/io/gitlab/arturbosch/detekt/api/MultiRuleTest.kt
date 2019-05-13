package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
internal class MultiRuleTest : Spek({

    describe("a multi rule can have filters") {

        fun ruleSet() = RuleSet("TestMultiRule", listOf(TestMultiRule()))
        val file = compileForTest(Case.FilteredClass.path())

        it("should not run any rules if both are filtered out") {
            val filters = setOf("TestRuleOne", "TestRuleTwo")
            val findings = ruleSet().accept(file, filters)

            assertThat(findings).isEmpty()
        }

        it("should only run one rule as the other is filtered") {
            val filters = setOf("TestRuleOne")
            val findings = ruleSet().accept(file, filters)

            assertThat(findings).hasSize(1)
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
    override val issue: Issue = Issue(javaClass.simpleName, Severity.Minor, "", Debt.TWENTY_MINS)

    override fun visitKtFile(file: KtFile) {
        report(CodeSmell(issue, Entity.from(file), message = ""))
    }
}

class TestRuleOne : AbstractRule()
class TestRuleTwo : AbstractRule()
