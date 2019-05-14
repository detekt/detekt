package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RuleSetProviderSpec : Spek({

    fun ruleSetInstance(config: Config) = TestProvider().buildRuleset(config)
        ?: throw AssertionError("Expected a rule set")

    describe("loads rule set") {

        context("rule set with path filters") {

            it("has no filters from an empty config") {
                assertThat(ruleSetInstance(Config.empty).pathFilters).isNull()
            }

            it("has filters from rule set entry in config") {
                val config = TestConfig(Config.EXCLUDES_KEY to "**/*.kt")
                assertThat(ruleSetInstance(config).pathFilters).isNotNull()
            }

            context("filtering by paths") {

                val file = compileForTest(Case.FilteredClass.path())

                it("excludes file and does not report") {
                    val config = TestConfig(Config.EXCLUDES_KEY to "**/*.kt")
                    assertThat(ruleSetInstance(config).accept(file)).isEmpty()
                }

                it("should include and report file") {
                    val config = TestConfig(
                        Config.EXCLUDES_KEY to "**/*.kt",
                        Config.INCLUDES_KEY to "**/*.kt"
                    )
                    assertThat(ruleSetInstance(config).accept(file)).hasSize(1)
                }
            }
        }
    }
})

class TestProvider : RuleSetProvider {

    override val ruleSetId: String = "test"

    override fun instance(config: Config): RuleSet = RuleSet(ruleSetId, listOf(Test()))
}

class Test : BaseRule() {

    override fun visitCondition(root: KtFile): Boolean = true

    override fun visit(root: KtFile) {
        report(
            CodeSmell(
                Issue(
                    "Test",
                    Severity.Style,
                    "",
                    Debt.FIVE_MINS
                ),
                Entity.from(root),
                ""
            )
        )
    }
}
