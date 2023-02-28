package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class MaxChainedCallsOnSameLineSpec(private val env: KotlinCoreEnvironment) {
    private val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 3))

    @Test
    fun `does not report 2 calls on a single line with a max of 3`() {
        val code = "val a = 0.plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report 3 calls on a single line with a max of 3`() {
        val code = "val a = 0.plus(0).plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports 4 calls on a single line with a max of 3`() {
        val code = "val a = 0.plus(0).plus(0).plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports 4 calls on a single line with a max of 3 but with inlined lambda`() {
        val code = "val a = 0.plus(0).let { it }.plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports 4 safe qualified calls on a single line with a max of 3`() {
        val code = "val a = 0?.plus(0)?.plus(0)?.plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports 1 safe qualified calls on a single line with a max of 1`() {
        val code = "val a = 0?.plus(0)"

        val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 1))
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0]).hasMessage(getTestMessage(2, 1))
    }

    @Test
    fun `reports 4 non-null asserted calls on a single line with a max of 3`() {
        val code = "val a = 0!!.plus(0)!!.plus(0)!!.plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports 1 non-null asserted calls on a single line with a max of 1`() {
        val code = "val a = 0!!.plus(0)"

        val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 1))
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0]).hasMessage(getTestMessage(2, 1))
    }

    @Test
    fun `reports once for 7 calls on a single line with a max of 3`() {
        val code = "val a = 0.plus(0).plus(0).plus(0).plus(0).plus(0).plus(0)"

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports once for 2 calls on a single line with a max of 1`() {
        val code = "val a = 0.plus(0)"

        val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 1))
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0]).hasMessage(getTestMessage(2, 1))
    }

    @Test
    fun `does not report 5 calls on separate lines with a max of 3`() {
        val code = """
            val a = 0
                .plus(0)
                .plus(0)
                .plus(0)
                .plus(0)
        """.trimIndent()

        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report 3 calls on same line with wrapped calls with a max of 3`() {
        val code = """
            val a = 0.plus(0).plus(0)
                .plus(0).plus(0).plus(0)
                .plus(0).plus(0).plus(0)
        """.trimIndent()

        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports 4 calls on same line with wrapped calls with a max of 3`() {
        val code = """
            val a = 0.plus(0).plus(0).plus(0)
                .plus(0)
                .plus(0)
        """.trimIndent()

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports 4 calls on wrapped line with with a max of 3`() {
        val code = """
            val a = 0
                .plus(0)
                .plus(0).plus(0).plus(0).plus(0)
                .plus(0)
        """.trimIndent()

        assertThat(rule.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report long imports`() {
        val code = "import a.b.c.d.e"

        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `does not report long package declarations`() {
        val code = "package a.b.c.d.e"

        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not count package references as chained calls`() {
        val code = """
            val x = kotlin.math.floor(1.0).plus(1).plus(1)
        """.trimIndent()
        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does report with package references as chained calls`() {
        val code = """
            val x = kotlin.math.floor(1.0).plus(1).plus(1).plus(1)
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0]).hasMessage(getTestMessage(4, 3))
    }

    @Test
    fun `does not count a package reference as chained calls`() {
        val code = """
            val x = kotlin.run { 1 }.plus(1).plus(1)
        """.trimIndent()
        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does report with a package reference as chained calls`() {
        val code = """
            @Suppress("RemoveRedundantQualifierName")
            val x = kotlin.run { 1 }.plus(1).plus(1).plus(1)
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0]).hasMessage(getTestMessage(4, 3))
    }

    @Test
    fun `does not report with property chained calls with 3 calls`() {
        val code = """
            import kotlin.math.ulp

            val x = 1.0.ulp.ulp
        """.trimIndent()
        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does report with property chained calls with 4 calls`() {
        val code = """
            import kotlin.math.ulp

            val x = 1.0.ulp.ulp.ulp
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0]).hasMessage(getTestMessage(4, 3))
    }

    @Test
    fun `does not report with property chained calls with 3 calls in separate lines with a max of 1`() {
        val code = """
            import kotlin.math.ulp

            val x = 1.0
                .ulp
                .ulp
                .ulp
        """.trimIndent()
        val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 1))
        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report with mix normal and property chained calls with 3 calls with a max of 3`() {
        val code = """
            import kotlin.math.ulp

            val x = 1.0.plus(0)
                .plus(0)
                .ulp.ulp.ulp
                .plus(0)
        """.trimIndent()
        assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does report with mix normal and property chained calls with 4 calls`() {
        val code = """
            import kotlin.math.ulp

            val x = 1.0.plus(0)
                .plus(0)
                .ulp.ulp.ulp.ulp
                .plus(0)
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0]).hasMessage(getTestMessage(4, 3))
    }

    @Nested
    inner class WithBracesOnNewLineStyle {
        @Test
        fun `does not report 5 calls on separate lines with a max of 1`() {
            val code = """
                val a = 0
                    .plus(
                        0
                    ).plus(
                        0
                    ).plus(
                        0
                    ).plus(
                        0
                    )
            """.trimIndent()
            val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 1))
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report 5 calls with one and two in same line with a max of 2`() {
            val code = """
                val a = 0.plus(
                    0
                ).plus(
                    0
                ).plus(
                    0
                ).plus(
                    0
                )
            """.trimIndent()
            val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 2))
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does report 2 calls with one and two in same line with a max of 1`() {
            val code = """
                val a = 0.plus(
                    0
                )
            """.trimIndent()
            val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 1))
            val findings = rule.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage(getTestMessage(2, 1))
        }

        @Test
        fun `does not report 5 calls on separate lines with inlined lambda with a max of 1`() {
            val code = """
                val a = 0
                    .plus(
                        0
                    ).let{
                        0
                    }.plus(
                        0
                    ).plus(
                        0
                    )
            """.trimIndent()
            val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 1))
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report 5 calls on separate lines with unsafe call with a max of 1`() {
            val code = """
                val a = 0
                    ?.plus(
                        0
                    )?.let{
                        0
                    }?.plus(
                        0
                    )?.plus(
                        0
                    )
            """.trimIndent()
            val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 1))
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report 5 calls on separate lines with double bang with a max of 1`() {
            val code = """
                val a = 0!!
                    .plus(
                        0
                    )!!.let{
                        0
                    }!!.plus(
                        0
                    )!!.plus(
                        0
                    )
            """.trimIndent()
            val rule = MaxChainedCallsOnSameLine(TestConfig("maxChainedCalls" to 1))
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report 3 calls on separate line mixed with the new style with a max of 3`() {
            val code = """
                val a = 0
                    .plus(
                        0
                    ).let{
                        0
                    }.plus(0).plus(0).plus(0)
            """.trimIndent()
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report 3 calls on separate line mixed with the new style ending with call in separate line`() {
            val code = """
                val a = 0
                    .plus(
                        0
                    ).let {
                        0
                    }.plus(0).plus(0).plus(0)
                    .plus(
                        1
                    )
            """.trimIndent()
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does report 4 calls on separate line mixed with new style`() {
            val code = """
                val a = 0
                    .plus(
                        0
                    ).plus(
                        0
                    ).plus(0).plus(0).plus(0).plus(
                        0
                    ).plus(
                        0
                    )
            """.trimIndent()
            val findings = rule.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage(getTestMessage(4, 3))
        }

        @Test
        fun `does report 4 calls on separate line after inlined lambda mixed with new style`() {
            val code = """
                val a = 0
                    .plus(
                        0
                    ).let{
                        0
                    }.plus(0).plus(0).plus(0).plus(
                        0
                    ).plus(
                        0
                    )
            """.trimIndent()
            val findings = rule.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage(getTestMessage(4, 3))
        }

        @Test
        fun `does not report with package references as chained calls ending with violation`() {
            val code = """
                val x = kotlin.math.floor(
                    1.0
                ).plus(
                    1
                ).plus(
                    1
                ).plus(
                    1
                ).plus(1).plus(1).plus(1)
            """.trimIndent()
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not count a package reference as chained calls`() {
            val code = """
                @Suppress("RemoveRedundantQualifierName")
                val x = kotlin.run {
                    1
                }.plus(
                    1
                ).plus(
                    1
                ).plus(
                    1
                ).plus(
                    1
                )
            """.trimIndent()
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report with a package reference and 3 chained variables `() {
            val code = """
                import kotlin.math.ulp

                @Suppress("RemoveRedundantQualifierName")
                val x = kotlin.run { 1.0 }.plus(
                    1
                ).ulp.ulp.ulp
            """.trimIndent()
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does report with a package reference and 4 chained variables violation`() {
            val code = """
                import kotlin.math.ulp

                @Suppress("RemoveRedundantQualifierName")
                val x = kotlin.run { 1.0 }.plus(
                    1
                ).ulp.ulp.ulp.ulp
            """.trimIndent()
            val findings = rule.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage(getTestMessage(4, 3))
        }
    }

    private fun getTestMessage(chainedCalls: Int, maxChainedCalls: Int): String {
        return "$chainedCalls chained calls on a single line; more than $maxChainedCalls calls should " +
            "be wrapped to a new line."
    }
}
