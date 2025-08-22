package dev.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnsafeCastSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = UnsafeCast(Config.empty)

    @Test
    fun `reports cast that cannot succeed`() {
        val code = """
            fun test(s: String) {
                println(s as Int)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports 'safe' cast that cannot succeed`() {
        val code = """
            fun test(s: String) {
                println((s as? Int) ?: 0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report cast that might succeed`() {
        val code = """
            fun test(s: Any) {
                println(s as Int)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report 'safe' cast that might succeed`() {
        val code = """
            fun test(s: Any) {
                println((s as? Int) ?: 0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
