package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.api.MaxIssuesReached
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class MaxIssueCheckSpec {

    @Nested
    inner class `based only on MaxIssuePolicy` {

        @Nested
        inner class `policy of any` {

            private val fixture = createFixture(RulesSpec.MaxIssuePolicy.AllowAny)

            @ParameterizedTest(name = "passes on {0} issues")
            @ValueSource(ints = [-1, 0, 1, 100])
            fun `passes on issues`(value: Int) {
                assertThatCode { fixture.check(value) }.doesNotThrowAnyException()
            }
        }

        @Nested
        inner class `policy of none` {

            private val fixture = createFixture(RulesSpec.MaxIssuePolicy.NoneAllowed)

            @Test
            fun `passes on zero issues`() {
                assertThatCode { fixture.check(0) }.doesNotThrowAnyException()
            }

            @ParameterizedTest(name = "fails on {0} issues")
            @ValueSource(ints = [-1, 1, 100])
            fun `fails on issues`(value: Int) {
                assertThatCode { fixture.check(value) }.isInstanceOf(MaxIssuesReached::class.java)
            }
        }

        @Nested
        inner class `policy of specified amount of 2 issues` {

            private val fixture = createFixture(RulesSpec.MaxIssuePolicy.AllowAmount(2))

            @ParameterizedTest(name = "passes on {0} issues")
            @ValueSource(ints = [-1, 0, 1, 2])
            fun `passes on issues`(value: Int) {
                assertThatCode { fixture.check(value) }.doesNotThrowAnyException()
            }

            @ParameterizedTest(name = "fails on {0} issues")
            @ValueSource(ints = [3, 100])
            fun `fails on issues`(value: Int) {
                assertThatCode { fixture.check(value) }.isInstanceOf(MaxIssuesReached::class.java)
            }
        }
    }

    @Nested
    inner class `based on config` {

        val config = yamlConfigFromContent(
            """
                build:
                    maxIssues: 1
            """.trimIndent()
        )

        @Test
        fun `uses the config for max issues when MaxIssuePolicy == NonSpecified`() {
            val fixture = MaxIssueCheck(
                ProcessingSpec { rules { maxIssuePolicy = RulesSpec.MaxIssuePolicy.NonSpecified } }.rulesSpec,
                config
            )

            assertThatCode { fixture.check(1) }.doesNotThrowAnyException()
        }

        @Test
        fun `skips the config on any other Policy specied`() {
            val fixture = MaxIssueCheck(
                ProcessingSpec { rules { maxIssuePolicy = RulesSpec.MaxIssuePolicy.NoneAllowed } }.rulesSpec,
                config
            )

            assertThatCode { fixture.check(1) }.isInstanceOf(MaxIssuesReached::class.java)
        }
    }
}

private fun createFixture(policy: RulesSpec.MaxIssuePolicy) =
    MaxIssueCheck(ProcessingSpec { rules { maxIssuePolicy = policy } }.rulesSpec, Config.empty)
