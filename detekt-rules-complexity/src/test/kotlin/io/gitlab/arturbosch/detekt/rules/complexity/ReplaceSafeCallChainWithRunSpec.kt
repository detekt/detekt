package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ReplaceSafeCallChainWithRunSpec(val env: KotlinCoreEnvironment) {

    val subject = ReplaceSafeCallChainWithRun()

    @Test
    fun `reports long chain of unnecessary safe qualified expressions`() {
        val code = """
            val x: String? = "string"

            val y = x
                ?.asSequence()
                ?.map { it }
                ?.distinctBy { it }
                ?.iterator()
                ?.forEach(::println)
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports short chain of unnecessary safe qualified expressions`() {
        val code = """
            val x: String? = "string"

            val y = x
                ?.asSequence()
                ?.map { it }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report a safe call chain which is too short to benefit`() {
        val code = """
            val x: String? = "string"

            val y = x
                ?.asSequence()
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report a safe call chain on left side of assignment`() {
        val code = """
            class Something {
                var element: Element? = null
            }

            class Element(var list: List<String>?)

            val z: Something? = Something()

            fun modifyList() {
                z?.element?.list = listOf("strings")
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
