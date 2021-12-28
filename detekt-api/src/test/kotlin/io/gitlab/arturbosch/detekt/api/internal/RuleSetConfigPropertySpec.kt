package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RuleSetConfigPropertySpec : Spek({

    describe("Rule set config property delegate") {
        context("boolean property") {
            it("reads the value in config if present") {
                assertThat(TestRuleSetProvider.android.value(TestConfig("android" to "false")))
                    .isEqualTo(false)
            }
            it("uses the default value in config if not present") {
                assertThat(TestRuleSetProvider.android.value(TestConfig()))
                    .isEqualTo(true)
            }
        }

        context("int property") {
            it("reads the value in config if present") {
                assertThat(TestRuleSetProvider.number.value(TestConfig("number" to "37"))).isEqualTo(37)
            }
            it("uses the default value in config if not present") {
                assertThat(TestRuleSetProvider.number.value(TestConfig())).isEqualTo(42)
            }
        }

        context("string property") {
            it("reads the value in config if present") {
                assertThat(TestRuleSetProvider.fileName.value(TestConfig("fileName" to "main.kt")))
                    .isEqualTo("main.kt")
            }
            it("uses the default value in config if not present") {
                assertThat(TestRuleSetProvider.fileName.value(TestConfig()))
                    .isEqualTo("test.kt")
            }
        }
    }
})

private class TestRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "testRuleSetId"

    override fun instance(config: Config): RuleSet = RuleSet(ruleSetId, emptyList())

    companion object {
        val android by ruleSetConfig(true)
        val fileName by ruleSetConfig("test.kt")
        val number by ruleSetConfig(42)
    }
}
