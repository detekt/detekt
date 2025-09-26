package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.api.Rule
import dev.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.argumentSet
import org.junit.jupiter.params.provider.MethodSource

class ConfigPropertySpec {
    private val androidRulesetConfig = TestConfig("code_style" to "android_studio")
    private val nonAndroidRulesetConfig = TestConfig("code_style" to "intellij_idea")
    private val androidUndefinedRulesetConfig = TestConfig()

    @Test
    fun `uses default if android property of ruleset is not defined`() {
        val subject = object : AndroidTestRule(androidUndefinedRulesetConfig) {
            val configValue: String by configWithAndroidVariants("default", "android")
        }

        assertThat(subject.configValue).isEqualTo("default")
    }

    @Test
    fun `uses default if ruleset is non android`() {
        val subject = object : AndroidTestRule(nonAndroidRulesetConfig) {
            val configValue: String by configWithAndroidVariants("default", "android")
        }

        assertThat(subject.configValue).isEqualTo("default")
    }

    @Test
    fun `uses android default if ruleset is android`() {
        val subject = object : AndroidTestRule(androidRulesetConfig) {
            val configValue: String by configWithAndroidVariants("default", "android")
        }

        assertThat(subject.configValue).isEqualTo("android")
    }

    @ParameterizedTest
    @MethodSource("rulesetConfigs")
    fun `always uses explicitly defined value from config`(rulesetConfig: Config) {
        val explicitlyConfiguredValue = "other"
        val subject = object : AndroidTestRule(rulesetConfig, "configValue" to explicitlyConfiguredValue) {
            val configValue: String by configWithAndroidVariants("default", "android")
        }

        assertThat(subject.configValue).isEqualTo(explicitlyConfiguredValue)
    }

    fun rulesetConfigs(): List<Arguments.ArgumentSet> = listOf(
        argumentSet("ruleset has android = true", androidRulesetConfig),
        argumentSet("ruleset has android = false", nonAndroidRulesetConfig),
        argumentSet("ruleset has no value for android property", androidUndefinedRulesetConfig)
    )

    private open class AndroidTestRule(rulesetConfig: Config, vararg ruleConfig: Pair<String, Any>) :
        Rule(TestConfig(parent = rulesetConfig, *ruleConfig), "description")
}
