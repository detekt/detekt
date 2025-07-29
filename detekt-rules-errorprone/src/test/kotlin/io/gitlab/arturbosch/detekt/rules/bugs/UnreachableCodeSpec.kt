package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnreachableCodeSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = UnreachableCode(Config.empty)

    @Test
    fun `reports unreachable code after return`() {
        val code = """
            fun f(i: Int) {
                if (i == 0) {
                    return
                    println()
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports unreachable code after return in lambda`() {
        val code = """
            fun f(s: String): Boolean {
                s.let {
                    return it.length < 3
                    println()
                }
                return false
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `reports unreachable code after return with label`() {
        val code = """
            fun f(ints: List<Int>): List<Int> {
                return ints.map f@{
                    if (it == 0) {
                        return@f 0
                        println()
                    }
                    return@f 1
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports unreachable code after throwing an exception`() {
        val code = """
            fun f(i: Int) {
                if (i == 0) {
                    throw IllegalArgumentException()
                    println()
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports unreachable code after break and continue`() {
        val code = """
            fun f() {
                for (i in 1..2) {
                    break
                    println()
                }
                for (i in 1..2) {
                    continue
                    println()
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `does not report reachable code after conditional return with label`() {
        val code = """
            fun f(ints: List<Int>) {
                ints.forEach {
                    if (it == 0) return@forEach
                    println()
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report reachable code after if`() {
        val code = """
            fun f(i: Int) {
                if (i == 0) {
                    println()
                }
                throw IllegalArgumentException()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report reachable code in if body`() {
        val code = """
            fun f(i: Int) {
                if (i == 0) {
                    println(i)
                    throw IllegalArgumentException()
                }
                println()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports unreachable code after if expression`() {
        val code = """
            fun test(b: Boolean): Int {
                if (b) {
                    return 1
                } else {
                    return 2
                }
                return 0
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports unreachable code after when expression`() {
        val code = """
            enum class E { A, B }
            
            fun test(e: E): Int {
                when (e) {
                    E.A -> return 1
                    E.B -> return 2
                }
                return 0
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports unreachable code after try expression`() {
        val code = """
            fun test(): Int {
                try {
                    return 1
                } catch (e: Exception) {
                    throw e
                }
                return 0
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports unreachable code after elvis`() {
        val code = """
            fun test() {
                val a = 2 ?: run {
                    3
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }
}
