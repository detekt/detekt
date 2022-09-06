package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseCheckNotNullSpec(val env: KotlinCoreEnvironment) {
    val subject = UseCheckNotNull()

    @Test
    fun `reports 'check' calls with a non-null check`() {
        val code = """
            fun test(i: Int?) {
                check(i != null)
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports 'check' calls with a non-null check that has 'null' on the left side`() {
        val code = """
            fun test(i: Int?) {
                check(null != i)
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does not report a 'check' call without a non-null check`() {
        val code = """
            fun test(i: Int) {
                check(i > 0)
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }
}
