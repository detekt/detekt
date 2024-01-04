package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnsafeCastSpec(private val env: KotlinCoreEnvironment) {
    private val subject = UnsafeCast(Config.empty)

    @Test
    fun `reports cast that cannot succeed`() {
        val code = """
            fun test(s: String) {
                println(s as Int)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports 'safe' cast that cannot succeed`() {
        val code = """
            fun test(s: String) {
                println((s as? Int) ?: 0)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report cast that might succeed`() {
        val code = """
            fun test(s: Any) {
                println(s as Int)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report 'safe' cast that might succeed`() {
        val code = """
            fun test(s: Any) {
                println((s as? Int) ?: 0)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
