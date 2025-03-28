package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ForbiddenSuppressSpec {

    @Nested
    inner class `checking for suppressions of rule ARule` {
        private val subject = ForbiddenSuppress(
            TestConfig("rules" to listOf("ARule"))
        )

        @Test
        fun `supports java suppress annotations`() {
            val code = """
                @SuppressWarnings("ARule")
                class Foo
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasStartSourceLocation(1, 1)
            assertThat(findings.first()).hasMessage(
                "Cannot @Suppress rule \"ARule\" due to the current configuration."
            )
        }

        @Test
        fun `reports file-level suppression of forbidden rule`() {
            val code = """
                @file:Suppress("ARule")

                class A
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasStartSourceLocation(1, 1)
            assertThat(findings.first()).hasMessage(
                "Cannot @Suppress rule \"ARule\" due to the current configuration."
            )
        }

        @Test
        fun `reports top-level suppression of forbidden rule`() {
            val code = """
                @Suppress("ARule")
                fun foo() { }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasStartSourceLocation(1, 1)
            assertThat(findings.first()).hasMessage(
                "Cannot @Suppress rule \"ARule\" due to the current configuration."
            )
        }

        @Test
        fun `reports line-level suppression of forbidden rule`() {
            val code = """
                fun foo() {
                    @Suppress("ARule")
                    println("bar")
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasStartSourceLocation(2, 5)
            assertThat(findings.first()).hasMessage(
                "Cannot @Suppress rule \"ARule\" due to the current configuration."
            )
        }

        @Test
        fun `doesn't report non-forbidden rule`() {
            val code = """
                @Suppress("UNCHECKED_CAST")
                fun foo() { }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not include non-forbidden rule in report`() {
            val code = """
                @Suppress("UNCHECKED_CAST", "ARule")
                fun foo() { }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first()).hasMessage(
                "Cannot @Suppress rule \"ARule\" due to the current configuration."
            )
        }

        @Test
        fun `runs and does not report suppress without rules`() {
            val code = """
                @file:Suppress()

                class A

                @Suppress
                fun foo() { }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `checking multiple forbidden rules` {
        private val subject = ForbiddenSuppress(
            TestConfig("rules" to listOf("ARule", "BRule"))
        )

        @Test
        fun `reports suppression of both forbidden rules`() {
            val code = """
                @file:Suppress("ARule", "BRule")
                class A
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasStartSourceLocation(1, 1)
            assertThat(findings.first()).hasMessage(
                "Cannot @Suppress rules \"ARule\", \"BRule\" " +
                    "due to the current configuration."
            )
        }

        @Test
        fun `reports method-level suppression of one of two forbidden rules`() {
            val code = """
                @Suppress("BRule")
                fun foo() { }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasStartSourceLocation(1, 1)
            assertThat(findings.first()).hasMessage(
                "Cannot @Suppress rule \"BRule\" due to the current configuration."
            )
        }
    }

    @Nested
    inner class `checking suppression of self` {
        private val subject = ForbiddenSuppress(
            TestConfig("rules" to listOf("ForbiddenSuppress", "ARule"))
        )

        @Test
        fun `does not catch self-suppression`() {
            val code = """
                @Suppress("ForbiddenSuppress")
                class Foo
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `cannot be suppressed`() {
            val code = """
                @Suppress("ForbiddenSuppress", "ARule")
                class Foo
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `checking active with no rules defined` {
        private val subject = ForbiddenSuppress(TestConfig())

        @Test
        fun `will not report issues with no forbidden rules defined`() {
            val code = """
                @file:Suppress("ARule")
                class A
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }
    }
}
