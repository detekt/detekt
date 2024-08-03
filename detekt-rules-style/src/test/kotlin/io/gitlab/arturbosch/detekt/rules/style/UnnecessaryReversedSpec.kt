package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryReversedSpec(
    val env: KotlinCoreEnvironment,
) {
    val subject = UnnecessaryReversed(Config.empty)

    @Test
    @Suppress("IgnoredReturnValue")
    fun `reports when sortedBy is followed by asReversed()`() {
        val code =
            """
            fun foo() {
             val bar = listOf(1, 2, 3)
              .sorted()
                 .asReversed()
                }
            """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty
            .hasStartSourceLocation(4, 7)
            .withFailMessage("Replace `sorted().asReversed()` by single `sortedDescending()`")
    }

    @Test
    @Suppress("IgnoredReturnValue")
    fun `reports when sortedBy is followed by reversed()`() {
        val code =
            """
            fun foo() {
                val bar = listOf(1, 2, 3)
                    .sorted()
                    .reversed()
             }
            """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty
            .hasStartSourceLocation(4, 10)
            .withFailMessage("Replace `sorted().reversed()` by single `sortedDescending()`")
            .isNotNull()
    }

    @Test
    @Suppress("IgnoredReturnValue")
    fun `reports when sortedDescending is followed by asReversed()`() {
        val code =
            """
            fun foo() {
                val bar = listOf(1, 2, 3)
                    .sortedDescending()
                    .asReversed()
             }
            """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty
            .hasStartSourceLocation(4, 10)
            .withFailMessage("Replace `sortedDescending().asReversed()` by single `sorted()`")
            .isNotNull()
    }

    @Test
    @Suppress("IgnoredReturnValue")
    fun `reports when sortedDescending is followed by reversed()`() {
        val code =
            """
            fun foo() {
                val bar = listOf(1, 2, 3)
                    .sortedDescending()
                    .reversed()
             }
            """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty
            .hasStartSourceLocation(4, 10)
            .withFailMessage("Replace `sortedDescending().reversed()` by single `sorted()`")
            .isNotNull()
    }
}
