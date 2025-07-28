package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryReversedSpec(
    val env: KotlinEnvironmentContainer,
) {
    val subject = UnnecessaryReversed(Config.empty)

    @Test
    fun `reports when sortedBy is followed by asReversed()`() {
        val code =
            """
            fun foo() {
             val bar = listOf(1, 2, 3)
              .sorted()
                 .asReversed()
                }
            """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty
            .hasStartSourceLocation(4, 7)
            .withFailMessage("Replace `sorted().asReversed()` by single `sortedDescending()`")
            .isNotNull()
    }

    @Test
    fun `does not report single sort operation`() {
        val code =
            """
            fun foo() {
             val bar = listOf(1, 2, 3)
              .sorted()
              .map { }
            }
            """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings)
            .isEmpty()
    }

    @Test
    fun `does not report single reverse operation`() {
        val code =
            """
            fun foo() {
             val bar = listOf(1, 2, 3)
              .reversed()
              .map { }
            }
            """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings)
            .isEmpty()
    }

    @Test
    fun `reports when sortedBy is followed by reversed()`() {
        val code =
            """
            fun foo() {
                val bar = listOf(1, 2, 3)
                    .sorted()
                    .reversed()
             }
            """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty
            .hasStartSourceLocation(4, 10)
            .withFailMessage("Replace `sorted().reversed()` by single `sortedDescending()`")
            .isNotNull()
    }

    @Test
    fun `reports when reverse operation is followed by a sort`() {
        val code =
            """
            fun foo() {
                val bar = listOf(1, 2, 3)
                    .reversed()
                    .sorted()
             }
            """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty
            .hasStartSourceLocation(4, 10)
            .withFailMessage("Replace `sorted().reversed()` by single `sortedDescending()`")
            .isNotNull()
    }

    @Test
    fun `reports when sortedDescending is followed by asReversed()`() {
        val code =
            """
            fun foo() {
                val bar = listOf(1, 2, 3)
                    .sortedDescending()
                    .asReversed()
             }
            """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty
            .hasStartSourceLocation(4, 10)
            .withFailMessage("Replace `sortedDescending().asReversed()` by single `sorted()`")
            .isNotNull()
    }

    @Test
    fun `reports when sortedDescending is followed by reversed()`() {
        val code =
            """
            fun foo() {
                val bar = listOf(1, 2, 3)
                    .sortedDescending()
                    .reversed()
             }
            """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty
            .hasStartSourceLocation(4, 10)
            .withFailMessage("Replace `sortedDescending().reversed()` by single `sorted()`")
            .isNotNull()
    }

    @Test
    fun `reports when reverse operation is followed by a non immediate sort`() {
        val code =
            """
            fun foo() {
                val bar = listOf(1, 2, 3)
                    .reversed()
                    .map{ it * 2 }
                    .sorted()
             }
            """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty()
            .withFailMessage("Replace `sorted()` following `reversed()` by a single `sortedDescending() call`")
            .isNotNull()
    }

    @Test
    fun `reports when sort operation is followed by a non immediate reverse`() {
        val code =
            """
            fun foo() {
                val bar = listOf(1, 2, 3)
                    .sorted()
                    .map{ it * 2 }
                    .reversed()
             }
            """.trimIndent()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings)
            .isNotEmpty()
            .withFailMessage("Replace `reversed()` following `sorted()` by a single `sortedDescending() call`")
            .isNotNull()
    }
}
