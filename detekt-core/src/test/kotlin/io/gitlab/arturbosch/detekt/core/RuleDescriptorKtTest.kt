package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleInstance
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.Severity.Error
import io.gitlab.arturbosch.detekt.api.Severity.Info
import io.gitlab.arturbosch.detekt.api.Severity.Warning
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.ThrowingConsumer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.net.URI

@TestInstance(Lifecycle.PER_METHOD)
class RuleDescriptorKtTest {
    private val stringBuilder = StringBuilder()
    private val log: (() -> String) -> Unit = { message -> stringBuilder.appendLine(message.invoke()) }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun emptyConfigReturnsNoRule(fullAnalysis: Boolean) {
        val rules = getRules(
            fullAnalysis,
            listOf(TestDefaultRuleSetProvider()),
            Config.empty,
            log,
        )
        assertThat(rules).isEqualTo(emptyList<RuleDescriptor>())
        assertThat(stringBuilder.toString()).isEmpty()
    }

    @Test
    fun returns4RulesAndIgnoreUnknownRule() {
        val rules = getRules(
            true,
            listOf(TestDefaultRuleSetProvider()),
            yamlConfigFromContent(
                """
                    custom:
                      OneRule:
                        active: true
                      OneRule/foo:
                        active: true
                      AnotherRule:
                        active: false
                      RequiresFullAnalysisRule:
                        active: true
                      UnknownRule:
                        active: true
                """.trimIndent()
            ),
            log,
        )

        assertThat(rules)
            .satisfiesExactlyInAnyOrder(
                RuleDescriptionMatcher(::OneRule, configActive, true),
                RuleDescriptionMatcher(::OneRule, configActive, true, "OneRule/foo"),
                RuleDescriptionMatcher(::AnotherRule, configInactive, false),
                RuleDescriptionMatcher(::RequiresFullAnalysisRule, configActive, true),
            )
        assertThat(stringBuilder.toString()).isEmpty()
    }

    @Test
    fun doesntCrashWhenConfigHasWrongType() {
        val rules = getRules(
            true,
            listOf(TestDefaultRuleSetProvider()),
            yamlConfigFromContent(
                """
                    custom:
                      OneRule:
                        active: true
                        aConfiguration: 'abc'
                      OneRule/foo:
                        active: true
                      AnotherRule:
                        active: false
                      RequiresFullAnalysisRule:
                        active: true
                      UnknownRule:
                        active: true
                """.trimIndent()
            ),
            log,
        )

        assertThat(rules)
            .satisfiesExactlyInAnyOrder(
                RuleDescriptionMatcher(::OneRule, configActive + ("aConfiguration" to "abc"), true),
                RuleDescriptionMatcher(::OneRule, configActive, true, "OneRule/foo"),
                RuleDescriptionMatcher(::AnotherRule, configInactive, false),
                RuleDescriptionMatcher(::RequiresFullAnalysisRule, configActive, true),
            )
        assertThat(stringBuilder.toString()).isEmpty()
    }

    @Test
    fun `when fullAnalysis is disabled the rules that require full analysis are inactive`() {
        val rules = getRules(
            false,
            listOf(TestDefaultRuleSetProvider()),
            yamlConfigFromContent(
                """
                    custom:
                      OneRule:
                        active: true
                      OneRule/foo:
                        active: true
                      AnotherRule:
                        active: true
                      RequiresFullAnalysisRule:
                        active: true
                      UnknownRule:
                        active: true
                """.trimIndent()
            ),
            log,
        )

        assertThat(rules)
            .satisfiesExactlyInAnyOrder(
                RuleDescriptionMatcher(::OneRule, configActive, true),
                RuleDescriptionMatcher(::OneRule, configActive, true, "OneRule/foo"),
                RuleDescriptionMatcher(::AnotherRule, configActive, true),
                RuleDescriptionMatcher(::RequiresFullAnalysisRule, configActive, false),
            )
        assertThat(stringBuilder.toString())
            .isEqualTo("The rule 'RequiresFullAnalysisRule' requires type resolution but it was run without it.\n")
    }

    @Test
    fun `when fullAnalysis is disabled but the rule is disabled we log nothing`() {
        val rules = getRules(
            false,
            listOf(TestDefaultRuleSetProvider()),
            yamlConfigFromContent(
                """
                    custom:
                      RequiresFullAnalysisRule:
                        active: false
                """.trimIndent()
            ),
            log,
        )

        assertThat(rules)
            .satisfiesExactlyInAnyOrder(
                RuleDescriptionMatcher(::RequiresFullAnalysisRule, configInactive, false),
            )
        assertThat(stringBuilder.toString()).isEmpty()
    }

    @Test
    fun whenRuleSetIsInactiveReturnsAllRuleAreDisabled() {
        val rules = getRules(
            false,
            listOf(TestDefaultRuleSetProvider()),
            yamlConfigFromContent(
                """
                    custom:
                      active: false
                      OneRule:
                        active: true
                      OneRule/foo:
                        active: true
                      AnotherRule:
                        active: true
                        maxLine: 30
                      RequiresFullAnalysisRule:
                        active: true
                """.trimIndent()
            ),
            log,
        )

        assertThat(rules)
            .satisfiesExactlyInAnyOrder(
                RuleDescriptionMatcher(::OneRule, configActive, false),
                RuleDescriptionMatcher(::OneRule, configActive, false, "OneRule/foo"),
                RuleDescriptionMatcher(::AnotherRule, configActive + ("maxLine" to 30), false),
                RuleDescriptionMatcher(::RequiresFullAnalysisRule, configActive, false),
            )
        assertThat(stringBuilder.toString()).isEmpty()
    }

    @Nested
    inner class Url {

        @Test
        fun whenRuleSetIsInactiveReturnsAllRuleAreDisabled() {
            val rules = getRules(
                false,
                listOf(TestCustomRuleSetProvider()),
                yamlConfigFromContent(
                    """
                        custom:
                          OneRule:
                            active: true
                          OneRule/foo:
                            active: true
                          AnotherRule:
                            active: true
                    """.trimIndent()
                ),
                log,
            )

            assertThat(rules)
                .satisfiesExactlyInAnyOrder(
                    RuleDescriptionMatcher(::OneRule, configActive, url = null),
                    RuleDescriptionMatcher(::OneRule, configActive, id = "OneRule/foo", url = null),
                    RuleDescriptionMatcher(::AnotherRule, configActive, url = "https://example.org/")
                )
            assertThat(stringBuilder.toString()).isEmpty()
        }
    }

    @Nested
    inner class SeverityTest {

        @ParameterizedTest
        @ValueSource(strings = ["warning", "WARNING", "wArNiNg"])
        fun ignoreCase(candidate: String) {
            val rules = getRules(
                false,
                listOf(TestDefaultRuleSetProvider()),
                yamlConfigFromContent(
                    """
                        custom:
                          active: false
                          OneRule:
                            active: true
                            severity: '$candidate'
                    """.trimIndent()
                ),
                log,
            )

            assertThat(rules)
                .singleElement()
                .satisfies(
                    RuleDescriptionMatcher(
                        ::OneRule,
                        configActive + ("severity" to candidate),
                        false,
                        severity = Warning,
                    ),
                )
            assertThat(stringBuilder.toString()).isEmpty()
        }

        @ParameterizedTest
        @EnumSource(Severity::class)
        fun supportsAll(severity: Severity) {
            val rules = getRules(
                false,
                listOf(TestDefaultRuleSetProvider()),
                yamlConfigFromContent(
                    """
                        custom:
                          active: false
                          OneRule:
                            active: true
                            severity: '$severity'
                    """.trimIndent()
                ),
                log,
            )

            assertThat(rules)
                .singleElement()
                .satisfies(
                    RuleDescriptionMatcher(
                        ::OneRule,
                        configActive + ("severity" to severity.toString()),
                        false,
                        severity = severity,
                    ),
                )
            assertThat(stringBuilder.toString()).isEmpty()
        }

        @Test
        fun unknownSeverityThrows() {
            assertThatThrownBy {
                getRules(
                    false,
                    listOf(TestDefaultRuleSetProvider()),
                    yamlConfigFromContent(
                        """
                            custom:
                              active: false
                              OneRule:
                                active: true
                                severity: 'Unknown'
                        """.trimIndent()
                    ),
                    log,
                )
            }
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessage("'Unknown' is not a valid Severity. Allowed values are [Error, Warning, Info]")

            assertThat(stringBuilder.toString()).isEmpty()
        }

        @Test
        fun severityOnRuleSet() {
            val rules = getRules(
                false,
                listOf(TestDefaultRuleSetProvider()),
                yamlConfigFromContent(
                    """
                        custom:
                          severity: 'Warning'
                          active: false
                          OneRule:
                            active: true
                            severity: 'Info'
                          OneRule/foo:
                            active: true
                    """.trimIndent()
                ),
                log,
            )

            assertThat(rules)
                .satisfiesExactlyInAnyOrder(
                    RuleDescriptionMatcher(
                        ::OneRule,
                        configActive + ("severity" to "Info"),
                        false,
                        severity = Info,
                    ),
                    RuleDescriptionMatcher(
                        ::OneRule,
                        configActive,
                        false,
                        id = "OneRule/foo",
                        severity = Warning,
                    ),
                )
            assertThat(stringBuilder.toString()).isEmpty()
        }
    }
}

private class RuleDescriptionMatcher(
    private val ruleProvider: (Config) -> Rule,
    private val config: Map<String, Any>,
    private val active: Boolean = true,
    private val id: String = ruleProvider(Config.empty).javaClass.simpleName,
    private val severity: Severity = Error,
    private val url: String? = "default",
) : ThrowingConsumer<RuleDescriptor> {
    override fun acceptThrows(ruleDescriptor: RuleDescriptor?) {
        assertThat(ruleDescriptor?.ruleProvider).isEqualTo(ruleProvider)
        assertThat(ruleDescriptor?.config?.toMap()).isEqualTo(config)
        assertThat(ruleDescriptor?.ruleInstance).isEqualTo(createRuleInstance(id, active, url, severity))
    }
}

private fun Config.toMap(): Map<String, Any?> = buildMap {
    subConfigKeys().forEach {
        put(it, valueOrNull(it))
    }
}

private val configActive = mapOf("active" to true)
private val configInactive = mapOf("active" to false)

private fun createRuleInstance(id: String, active: Boolean, url: String?, severity: Severity) = RuleInstance(
    id,
    RuleSet.Id("custom"),
    if (url == "default") {
        if (id.startsWith("AnotherRule")) {
            URI("https://example.org/")
        } else {
            URI("https://detekt.dev/docs/1.6.0/rules/custom#${id.substringBefore("/").lowercase()}")
        }
    } else {
        url?.let(::URI)
    },
    "${id.substringBefore("/")}Description",
    severity = severity,
    active = active
)

private class TestDefaultRuleSetProvider : DefaultRuleSetProvider {
    override val ruleSetId = RuleSet.Id("custom")
    override fun instance() = RuleSet(ruleSetId, listOf(::OneRule, ::AnotherRule, ::RequiresFullAnalysisRule))
}

private class TestCustomRuleSetProvider : RuleSetProvider {
    override val ruleSetId = RuleSet.Id("custom")
    override fun instance() = RuleSet(ruleSetId, listOf(::OneRule, ::AnotherRule, ::RequiresFullAnalysisRule))
}

private class OneRule(config: Config) : Rule(config, "OneRuleDescription") {
    @Suppress("unused")
    @Configuration("a configuration")
    private val aConfiguration by config(1)
}

private class AnotherRule(config: Config) : Rule(config, "AnotherRuleDescription", URI("https://example.org/"))

private class RequiresFullAnalysisRule(
    config: Config,
) : Rule(config, "RequiresFullAnalysisRuleDescription"), RequiresFullAnalysis
