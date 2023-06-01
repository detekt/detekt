package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnsafeCallOnNullableTypeSpec(private val env: KotlinCoreEnvironment) {
    private val subject = UnsafeCallOnNullableType()

    @Test
    fun `reports unsafe call on nullable type`() {
        val code = """
            fun test(str: String?) {
                println(str!!.length)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report unsafe call on platform type`() {
        val code = """
            import java.util.UUID
            
            val version = UUID.randomUUID()!!
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report safe call on nullable type`() {
        val code = """
            fun test(str: String?) {
                println(str?.length)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report safe call in combination with the elvis operator`() {
        val code = """
            fun test(str: String?) {
                println(str?.length ?: 0)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
