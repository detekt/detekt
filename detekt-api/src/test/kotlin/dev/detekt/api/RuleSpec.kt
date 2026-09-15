package dev.detekt.api

import dev.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RuleSpec {

    @ParameterizedTest(name = "rule set: {0}, rule: {1} -> {2}")
    @CsvSource(
        "undefined, undefined, false",
        "undefined, true,      true",
        "undefined, false,     false",
        "true,      undefined, true",
        "true,      true,      true",
        "true,      false,     false",
        "false,     undefined, false",
        "false,     true,      true",
        "false,     false,     false",
    )
    fun `resolves autoCorrect preferring the rule level over the rule set level`(
        ruleSetLevel: String,
        ruleLevel: String,
        expected: Boolean,
    ) {
        val rule = CorrectableRule(ruleConfig(ruleSetLevel, ruleLevel))

        assertThat(rule.autoCorrect).isEqualTo(expected)
    }

    @Test
    fun `does not autocorrect a rule which does not implement AutoCorrectable`() {
        val rule = PlainRule(ruleConfig(ruleSetLevel = "true", ruleLevel = "true"))

        assertThat(rule.autoCorrect).isFalse()
    }
}

private fun ruleConfig(ruleSetLevel: String, ruleLevel: String): Config {
    val ruleValues = buildMap {
        if (ruleLevel != "undefined") put(Config.AUTO_CORRECT_KEY, ruleLevel.toBooleanStrict())
    }
    val ruleSetValues = buildMap<String, Any> {
        if (ruleSetLevel != "undefined") put(Config.AUTO_CORRECT_KEY, ruleSetLevel.toBooleanStrict())
        put("SomeRule", ruleValues)
    }
    return TestConfig("some-rule-set" to ruleSetValues)
        .subConfig("some-rule-set")
        .subConfig("SomeRule")
}

private class CorrectableRule(config: Config) :
    Rule(config, ""),
    AutoCorrectable

private class PlainRule(config: Config) : Rule(config, "")
