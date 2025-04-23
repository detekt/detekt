package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ReplaceSafeCallChainWithRunSpec(val env: KotlinEnvironmentContainer) {

    val subject = ReplaceSafeCallChainWithRun(Config.empty)

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

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports short chain of unnecessary safe qualified expressions`() {
        val code = """
            val x: String? = "string"
            
            val y = x
                ?.asSequence()
                ?.map { it }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report a safe call chain which is too short to benefit`() {
        val code = """
            val x: String? = "string"
            
            val y = x
                ?.asSequence()
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
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

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
