package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.loadRuleSet
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class MultiRuleSpec {

    private val file = compileForTest(resourceAsPath("/cases/Default.kt"))

    @Test
    fun `should not run any rules if rule set defines the filter`() {
        val config = yamlConfig("/pathFilters/multi-rule-with-excludes-on-ruleset.yml")
        assertThat(config.subConfig("TestMultiRule").shouldAnalyzeFile(file)).isFalse()
    }

    @Test
    fun `should not run any rules if rule set defines the filter with string`() {
        val config = yamlConfig("/pathFilters/multi-rule-with-excludes-on-ruleset-string.yml")
        assertThat(config.subConfig("TestMultiRule").shouldAnalyzeFile(file)).isFalse()
    }

    @Test
    fun `should only run one rule as the other is filtered`() {
        val config = yamlConfig("/pathFilters/multi-rule-with-one-exclude.yml")
        assertThat(loadRuleSet<MultiRuleProvider>(config).visitFile(file)).hasSize(1)
    }

    @Test
    fun `should only run one rule as the other is filtered with string`() {
        val config = yamlConfig("/pathFilters/multi-rule-with-one-exclude-string.yml")
        assertThat(loadRuleSet<MultiRuleProvider>(config).visitFile(file)).hasSize(1)
    }

    @Test
    fun `should run both when no filter is applied`() {
        val config = yamlConfig("/pathFilters/multi-rule-without-excludes.yml")
        assertThat(loadRuleSet<MultiRuleProvider>(config).visitFile(file)).hasSize(2)
    }

    @Test
    fun `should run none when both rules are filtered`() {
        val config = yamlConfig("/pathFilters/multi-rule-with-excludes.yml")
        assertThat(loadRuleSet<MultiRuleProvider>(config).visitFile(file)).isEmpty()
    }

    @Test
    fun `should run none when both rules are filtered with string`() {
        val config = yamlConfig("/pathFilters/multi-rule-with-excludes-string.yml")
        assertThat(loadRuleSet<MultiRuleProvider>(config).visitFile(file)).isEmpty()
    }
}

private class MultiRuleProvider : RuleSetProvider {
    override val ruleSetId: String = "TestMultiRule"
    override fun instance(config: Config): RuleSet = RuleSet(ruleSetId, listOf(TestMultiRule(config)))
}

private class TestMultiRule(config: Config) :
    @Suppress("DEPRECATION")
    io.gitlab.arturbosch.detekt.api.MultiRule() {

    private val one = TestRuleOne(config)
    private val two = TestRuleTwo(config)
    override val rules: List<Rule> = listOf(one, two)

    override fun visitKtFile(file: KtFile) {
        one.runIfActive { visitKtFile(file) }
        two.runIfActive { visitKtFile(file) }
    }
}

@Suppress("detekt.UnnecessaryAbstractClass") // uses inherited members
private abstract class AbstractRule(config: Config) : Rule(config) {
    override val issue: Issue = Issue(javaClass.simpleName, Severity.Minor, "", Debt.TWENTY_MINS)
    override fun visitKtFile(file: KtFile) {
        report(CodeSmell(issue, Entity.from(file), message = ""))
    }
}

private class TestRuleOne(config: Config) : AbstractRule(config)
private class TestRuleTwo(config: Config) : AbstractRule(config)
