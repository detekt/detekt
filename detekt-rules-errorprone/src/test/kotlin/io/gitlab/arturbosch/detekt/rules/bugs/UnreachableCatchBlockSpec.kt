package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnreachableCatchBlockSpec(private val env: KotlinCoreEnvironment) {
    private val subject = UnreachableCatchBlock(Config.empty)

    @Test
    fun `reports a unreachable catch block that is after the super class catch block`() {
        val code = """
            fun test() {
                try {
                } catch (t: Throwable) {
                } catch (e: Exception) {
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(4, 7)
    }

    @Test
    fun `reports a unreachable catch block that is after the same class catch block`() {
        val code = """
            fun test() {
                try {
                } catch (e: Exception) {
                } catch (e: Exception) {
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(4, 7)
    }

    @Test
    fun `reports two unreachable catch blocks that is after the super class catch block`() {
        val code = """
            fun test() {
                try {
                } catch (e: RuntimeException) {
                } catch (e: IllegalArgumentException) {
                } catch (e: IllegalStateException) {
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(2)
        assertThat(findings).hasStartSourceLocations(
            SourceLocation(4, 7),
            SourceLocation(5, 7)
        )
    }

    @Test
    fun `does not report unreachable catch block`() {
        val code = """
            fun test() {
                try {
                } catch (e: IllegalArgumentException) {
                } catch (e: IllegalStateException) {
                } catch (e: RuntimeException) {
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
