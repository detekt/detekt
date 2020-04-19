package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.core.rules.shouldAnalyzeFile
import io.gitlab.arturbosch.detekt.core.rules.visitFile
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.loadRuleSet
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class MultiRuleSpec : Spek({

    describe("a multi rule") {

        val file = compileForTest(Case.FilteredClass.path())

        context("runs once on a KtFile for every rules and respects configured path filters") {

            it("should not run any rules if rule set defines the filter") {
                val config = yamlConfig("/pathFilters/multi-rule-with-excludes-on-ruleset.yml")
                assertThat(loadRuleSet<MultiRuleProvider>(config).shouldAnalyzeFile(file, config)).isFalse()
            }

            it("should not run any rules if rule set defines the filter with string") {
                val config = yamlConfig("/pathFilters/multi-rule-with-excludes-on-ruleset-string.yml")
                assertThat(loadRuleSet<MultiRuleProvider>(config).shouldAnalyzeFile(file, config)).isFalse()
            }

            it("should only run one rule as the other is filtered") {
                val config = yamlConfig("/pathFilters/multi-rule-with-one-exclude.yml")
                assertThat(loadRuleSet<MultiRuleProvider>(config).visitFile(file)).hasSize(1)
            }

            it("should only run one rule as the other is filtered with string") {
                val config = yamlConfig("/pathFilters/multi-rule-with-one-exclude-string.yml")
                assertThat(loadRuleSet<MultiRuleProvider>(config).visitFile(file)).hasSize(1)
            }

            it("should run both when no filter is applied") {
                val config = yamlConfig("/pathFilters/multi-rule-without-excludes.yml")
                assertThat(loadRuleSet<MultiRuleProvider>(config).visitFile(file)).hasSize(2)
            }

            it("should run none when both rules are filtered") {
                val config = yamlConfig("/pathFilters/multi-rule-with-excludes.yml")
                assertThat(loadRuleSet<MultiRuleProvider>(config).visitFile(file)).isEmpty()
            }

            it("should run none when both rules are filtered with string") {
                val config = yamlConfig("/pathFilters/multi-rule-with-excludes-string.yml")
                assertThat(loadRuleSet<MultiRuleProvider>(config).visitFile(file)).isEmpty()
            }
        }
    }
})

@Suppress("UnusedPrivateClass") // bug: doesn't resolve usage as generic type
private class MultiRuleProvider : RuleSetProvider {
    override val ruleSetId: String = "TestMultiRule"
    override fun instance(config: Config): RuleSet = RuleSet(ruleSetId, listOf(TestMultiRule(config)))
}

private class TestMultiRule(config: Config) : MultiRule() {

    private val one = TestRuleOne(config)
    private val two = TestRuleTwo(config)
    override val rules: List<Rule> = listOf(one, two)

    override fun visitKtFile(file: KtFile) {
        one.runIfActive { visitKtFile(file) }
        two.runIfActive { visitKtFile(file) }
    }
}

private abstract class AbstractRule(config: Config) : Rule(config) {
    override val issue: Issue = Issue(javaClass.simpleName, Severity.Minor, "", Debt.TWENTY_MINS)
    override fun visitKtFile(file: KtFile) = report(CodeSmell(issue, Entity.from(file), message = ""))
}

private class TestRuleOne(config: Config) : AbstractRule(config)
private class TestRuleTwo(config: Config) : AbstractRule(config)
