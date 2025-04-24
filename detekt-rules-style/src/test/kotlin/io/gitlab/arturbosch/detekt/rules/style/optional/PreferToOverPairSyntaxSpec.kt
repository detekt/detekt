package io.gitlab.arturbosch.detekt.rules.style.optional

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class PreferToOverPairSyntaxSpec(val env: KotlinEnvironmentContainer) {
    val subject = PreferToOverPairSyntax(Config.empty)

    @Test
    fun `reports if pair is created using pair constructor`() {
        val code = """
            val pair1 = Pair(1, 2)
            val pair2: Pair<Int, Int> = Pair(1, 2)
            val pair3 = Pair(Pair(1, 2), Pair(3, 4))
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(5)
        assertThat(findings[0].message).endsWith("`1 to 2`.")
    }

    @Test
    fun `reports if pair is created using a function that uses pair constructor`() {
        val code = """
            val pair = createPair()
            fun createPair() = Pair(1, 2)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).endsWith("`1 to 2`.")
    }

    @Test
    fun `does not report if it is created using the to syntax`() {
        val code = "val pair = 1 to 2"
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report if a non-Kotlin Pair class was used`() {
        val code = """
            val pair1 = Pair(1, 2)
            val pair2: Pair<Int, Int> = Pair(1, 2)
            val pair3 = Pair(Pair(1, 2), Pair(3, 4))
            
            data class Pair<T, Z>(val int1: T, val int2: Z)
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report if pair is created using a function that uses the to syntax`() {
        val code = """
            val pair = createPair()
            fun createPair() = 1 to 2
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
