package io.gitlab.arturbosch.detekt.generator.printer.defaultconfig

import io.github.detekt.utils.yaml
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.valuesWithReason
import io.gitlab.arturbosch.detekt.generator.collection.Active
import io.gitlab.arturbosch.detekt.generator.collection.Configuration
import io.gitlab.arturbosch.detekt.generator.collection.DefaultValue
import io.gitlab.arturbosch.detekt.generator.collection.Inactive
import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class RuleSetConfigPrinterTest {
    private val ruleSetProviderTemplate = RuleSetProvider(
        name = "rulesetName",
        description = "description",
        defaultActivationStatus = Inactive
    )
    private val ruleTemplate = Rule(
        name = "ruleName",
        description = "",
        nonCompliantCodeExample = "",
        compliantCodeExample = "",
        defaultActivationStatus = Inactive,
        aliases = listOf("alias1", "alias2"),
        parent = ""
    )

    private val configurationTemplate = Configuration(
        name = "name",
        description = "description",
        defaultValue = DefaultValue.of(1),
        defaultAndroidValue = null,
        deprecated = null,
    )

    @Nested
    inner class PrintRuleSet {

        @Test
        fun `starts with name on top level`() {
            val ruleset = ruleSetProviderTemplate.copy(name = "rule-set-name")
            val actual = yaml { printRuleSet(ruleset, emptyList()) }
            assertThat(actual).startsWith("rule-set-name:\n")
        }

        @Test
        fun `includes all rules`() {
            val rules = listOf(ruleTemplate.copy(name = "RuleA"), ruleTemplate.copy(name = "RuleB"))
            val actual = yaml { printRuleSet(ruleSetProviderTemplate, rules) }
            assertThat(actual.lines()).contains("  RuleA:", "  RuleB:")
        }

        @Nested
        inner class ActivationStatus {

            @Test
            fun `has active property`() {
                val ruleset = ruleSetProviderTemplate.copy(defaultActivationStatus = Active("1.0.0"))
                val actual = yaml { printRuleSet(ruleset, emptyList()) }
                assertThat(actual.lines()).contains("  active: true")
            }

            @Test
            fun `has active property if inactive`() {
                val ruleset = ruleSetProviderTemplate.copy(defaultActivationStatus = Inactive)
                val actual = yaml { printRuleSet(ruleset, emptyList()) }
                assertThat(actual.lines()).contains("  active: false")
            }
        }
    }

    @Nested
    inner class PrintRule {

        @Test
        fun `starts with rule name`() {
            val rule = ruleTemplate.copy(name = "RuleA")
            val actual = yaml { printRule(rule) }
            assertThat(actual).startsWith("RuleA:\n")
        }

        @Nested
        inner class ActivationStatus {

            @Test
            fun `has active property`() {
                val rule = ruleTemplate.copy(defaultActivationStatus = Active("1.0.0"))
                val actual = yaml { printRule(rule) }
                assertThat(actual.lines()).contains("  active: true")
            }

            @Test
            fun `has active property if inactive`() {
                val rule = ruleTemplate.copy(defaultActivationStatus = Inactive)
                val actual = yaml { printRule(rule) }
                assertThat(actual.lines()).contains("  active: false")
            }
        }

        @Nested
        inner class AutoCorrect {

            @Test
            fun `has auto correct property`() {
                val rule = ruleTemplate.copy(autoCorrect = true)
                val actual = yaml { printRule(rule) }
                assertThat(actual.lines()).contains("  autoCorrect: true")
            }

            @Test
            fun `omits auto correct property if false`() {
                val rule = ruleTemplate.copy(autoCorrect = false)
                val actual = yaml { printRule(rule) }
                assertThat(actual).doesNotContain(Config.AUTO_CORRECT_KEY)
            }
        }

        @Nested
        inner class Exclusion {

            @Test
            fun `rule is excluded`() {
                val anExclusion = exclusions[0]
                val anExcludedRuleName = anExclusion.rules.first()
                val rule = ruleTemplate.copy(name = anExcludedRuleName)
                val actual = yaml { printRule(rule) }
                assertThat(actual.lines()).contains("  excludes: ${anExclusion.pattern}")
            }

            @Test
            fun `omits excludes property if rule is not excluded in any exclusion`() {
                val rule = ruleTemplate.copy(name = "ARuleNameThatIsNotExcluded")
                val actual = yaml { printRule(rule) }
                assertThat(actual).doesNotContain(Config.EXCLUDES_KEY)
            }
        }
    }

    @Nested
    inner class PrintConfiguration {

        @Test
        fun `ignore deprecated`() {
            val given = configurationTemplate.copy(deprecated = "use something else")
            val actual = yaml { printConfiguration(given) }
            assertThat(actual).isEmpty()
        }

        @Nested
        inner class DefaultValues {

            @Test
            fun `int default value`() {
                val given = configurationTemplate.copy(defaultValue = DefaultValue.of(99))
                val actual = yaml { printConfiguration(given) }
                assertThat(actual).isEqualTo("name: 99")
            }

            @Test
            fun `boolean default value`() {
                val given = configurationTemplate.copy(defaultValue = DefaultValue.of(false))
                val actual = yaml { printConfiguration(given) }
                assertThat(actual).isEqualTo("name: false")
            }

            @ValueSource(
                strings = [
                    "", " ", "a", "a b", "a\$b"
                ]
            )
            @ParameterizedTest
            fun `string default value is quoted`(value: String) {
                val given = configurationTemplate.copy(defaultValue = DefaultValue.of(value))
                val actual = yaml { printConfiguration(given) }
                assertThat(actual).isEqualTo("name: '$value'")
            }

            @Test
            fun `empty list default value uses array syntax`() {
                val given = configurationTemplate.copy(defaultValue = DefaultValue.of(emptyList()))
                val actual = yaml { printConfiguration(given) }
                assertThat(actual).isEqualTo("name: []")
            }

            @Test
            fun `string list default value`() {
                val given = configurationTemplate.copy(defaultValue = DefaultValue.of(listOf("a", "b", "c")))
                val actual = yaml { printConfiguration(given) }
                val expected = """
                    name:
                      - 'a'
                      - 'b'
                      - 'c'
                """.trimIndent()
                assertThat(actual).isEqualTo(expected)
            }

            @Test
            fun `empty ValuesWithReason default value uses empty list syntax`() {
                val given = configurationTemplate.copy(defaultValue = DefaultValue.of(valuesWithReason()))
                val actual = yaml { printConfiguration(given) }
                assertThat(actual).isEqualTo("name: []")
            }

            @Test
            fun `ValuesWithReason default value block syntax`() {
                val given = configurationTemplate.copy(
                    defaultValue = DefaultValue.of(
                        valuesWithReason(
                            "a" to "reason a",
                            "b" to null,
                            "c" to "reason c",
                        )
                    )
                )
                val actual = yaml { printConfiguration(given) }
                val expected = """
                    name:
                      - reason: 'reason a'
                        value: 'a'
                      - value: 'b'
                      - reason: 'reason c'
                        value: 'c'
                """.trimIndent()
                assertThat(actual).isEqualTo(expected)
            }
        }
    }
}
