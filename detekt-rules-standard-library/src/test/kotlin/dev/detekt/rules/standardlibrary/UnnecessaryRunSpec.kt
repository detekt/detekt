package dev.detekt.rules.standardlibrary

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryRunSpec(val env: KotlinEnvironmentContainer) {

    val subject = UnnecessaryRun(Config.empty)

    @Nested
    inner class `receiverless run with a single expression` {

        @Test
        fun `reports a run used as the initializer of a variable`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun calculateValue(): Int = 42

                    fun f() {
                        val value = run { calculateValue() }
                    }
                """.trimIndent()
            )
            assertThat(findings).singleElement()
                .hasMessage("run expression can be omitted")
        }

        @Test
        fun `reports a run used in a return statement`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f(result: Int): Int {
                        return run { result }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports a run used as a function argument`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun foo(value: Int) {}

                    fun f() {
                        foo(run { 1 })
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports a run whose single expression spans multiple lines`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f() {
                        val value = run {
                            listOf(1, 2, 3)
                                .filter { it > 1 }
                                .sum()
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports a run whose only statement has a preceding comment`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f() {
                        val value = run {
                            // a comment
                            1
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports a run whose single statement is an if expression`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f(flag: Boolean) {
                        val value = run {
                            if (flag) 1 else 2
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports a run whose single statement is a when expression`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f(flag: Boolean) {
                        val value = run {
                            when (flag) {
                                true -> 1
                                false -> 2
                            }
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports a run whose single statement is a try expression`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f() {
                        val value = run {
                            try {
                                1
                            } catch (e: Exception) {
                                2
                            }
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports a run used inside an elvis operator`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f(nullable: Int?): Int {
                        return nullable ?: run { 1 }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports a run used as an expression body function`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f(): Int = run { 1 }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports each receiverless run in a nested run`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f() {
                        val value = run { run { 1 } }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `reports the fully qualified kotlin run`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f() {
                        val value = kotlin.run { 1 }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `points to the run call expression`() {
            val findings = subject.lintWithContext(
                env,
                """
                    fun f() {
                        val value = run { 1 }
                    }
                """.trimIndent()
            )
            assertThat(findings).singleElement()
                .hasStartSourceLocation(2, 17)
        }
    }

    @Nested
    inner class `compliant usages` {

        @Test
        fun `does not report an empty run`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun f() {
                            run { }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report a run with multiple statements`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun first() {}
                        fun second() {}

                        fun f() {
                            val value = run {
                                first()
                                second()
                            }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report a run with multiple statements separated by semicolons`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun f() {
                            val value = run { 1; 2 }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report a run whose only statement is a local declaration`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun f() {
                            run {
                                val x = 1
                            }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report run with an explicit receiver`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun calculateValue(): Int = 42

                        fun f() {
                            val receiver = 1
                            val value = receiver.run { calculateValue() }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report run with a safe-call receiver`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun f() {
                            val receiver: Int? = 1
                            val value = receiver?.run { this + 1 }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report run with a parenthesized receiver`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun f() {
                            val value = (1).run { this + 1 }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report with`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        class Receiver {
                            fun calculateValue(): Int = 1
                        }

                        fun f(receiver: Receiver) {
                            val value = with(receiver) { calculateValue() }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report let`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun f() {
                            val receiver = 1
                            val value = receiver.let { it + 1 }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report apply`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        class Receiver {
                            var prop: Int = 0
                        }

                        fun f(receiver: Receiver) {
                            receiver.apply { prop = 1 }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report a user-defined run function`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun run(block: () -> Int): Int = block()

                        fun f() {
                            val value = run { 1 }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report a member function named run on a custom receiver`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        class Receiver {
                            fun run(block: () -> Int): Int = block()
                        }

                        fun f(receiver: Receiver) {
                            val value = receiver.run { 1 }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report a shadowed run variable invoked without a trailing lambda`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun f() {
                            val run = { 1 }
                            val value = run()
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `does not report an aliased import of run`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        import kotlin.run as runAlias

                        fun f() {
                            val value = runAlias { 1 }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }
    }
}
