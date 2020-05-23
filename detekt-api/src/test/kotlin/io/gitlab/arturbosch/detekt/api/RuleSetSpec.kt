package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.core.rules.createRuleSet
import io.gitlab.arturbosch.detekt.core.rules.isActive
import io.gitlab.arturbosch.detekt.core.rules.shouldAnalyzeFile
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.github.detekt.test.utils.compileForTest
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RuleSetSpec : Spek({

    describe("rule sets") {

        context("should rule set be used") {

            it("is explicitly deactivated") {
                val config = yamlConfig("detekt.yml")
                assertThat(TestProvider().isActive(config)).isFalse()
            }

            it("is active with an empty config") {
                assertThat(TestProvider().isActive(Config.empty)).isTrue()
            }
        }

        context("should rule analyze a file") {

            val file = compileForTest(Case.FilteredClass.path())

            it("analyzes file with an empty config") {
                val config = Config.empty
                assertThat(ruleSetInstance(config).shouldAnalyzeFile(file, config)).isTrue()
            }

            it("should not analyze file with *.kt excludes") {
                val config = TestConfig(Config.EXCLUDES_KEY to "**/*.kt")
                assertThat(ruleSetInstance(config).shouldAnalyzeFile(file, config)).isFalse()
            }

            it("should analyze file as it's path is first excluded but then included") {
                val config = TestConfig(
                    Config.EXCLUDES_KEY to "**/*.kt",
                    Config.INCLUDES_KEY to "**/*.kt"
                )
                assertThat(ruleSetInstance(config).shouldAnalyzeFile(file, config)).isTrue()
            }
        }
    }
})

private class Test : Rule() {
    override val issue: Issue = Issue("Test", Severity.Style, "", Debt.FIVE_MINS)
    override fun visit(root: KtFile) {
        report(CodeSmell(issue, Entity.from(root), ""))
    }
}

private class TestProvider : RuleSetProvider {

    override val ruleSetId: String = "comments"
    override fun instance(config: Config): RuleSet = RuleSet(ruleSetId, listOf(Test()))
}

private fun ruleSetInstance(config: Config) = TestProvider().createRuleSet(config)
