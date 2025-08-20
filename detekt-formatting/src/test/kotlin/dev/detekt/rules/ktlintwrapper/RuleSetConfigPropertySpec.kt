package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetProvider
import dev.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RuleSetConfigPropertySpec {

    @Nested
    inner class `boolean property` {
        @Test
        fun `reads the value in config if present`() {
            assertThat(TestRuleSetProvider.android.value(TestConfig("android" to "false")))
                .isEqualTo(false)
        }

        @Test
        fun `uses the default value in config if not present`() {
            assertThat(TestRuleSetProvider.android.value(TestConfig()))
                .isEqualTo(true)
        }
    }

    @Nested
    inner class `int property` {
        @Test
        fun `reads the value in config if present`() {
            assertThat(TestRuleSetProvider.number.value(TestConfig("number" to "37"))).isEqualTo(37)
        }

        @Test
        fun `uses the default value in config if not present`() {
            assertThat(TestRuleSetProvider.number.value(TestConfig())).isEqualTo(42)
        }
    }

    @Nested
    inner class `string property` {
        @Test
        fun `reads the value in config if present`() {
            assertThat(TestRuleSetProvider.fileName.value(TestConfig("fileName" to "main.kt")))
                .isEqualTo("main.kt")
        }

        @Test
        fun `uses the default value in config if not present`() {
            assertThat(TestRuleSetProvider.fileName.value(TestConfig()))
                .isEqualTo("test.kt")
        }
    }
}

private class TestRuleSetProvider : RuleSetProvider {
    override val ruleSetId = RuleSet.Id("testRuleSetId")

    override fun instance(): RuleSet = RuleSet(ruleSetId, emptyList())

    companion object {
        val android by ruleSetConfig(true)
        val fileName by ruleSetConfig("test.kt")
        val number by ruleSetConfig(42)
    }
}
