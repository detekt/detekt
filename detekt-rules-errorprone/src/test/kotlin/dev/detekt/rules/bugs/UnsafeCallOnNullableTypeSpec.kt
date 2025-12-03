package dev.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.test.KotlinEnvironmentContainer
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnsafeCallOnNullableTypeSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = UnsafeCallOnNullableType(Config.empty)

    @Test
    fun `reports unsafe call on nullable type`() {
        val code = """
            fun test(str: String?) {
                println(str!!.length)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report unsafe call on platform type`() {
        val code = """
            import java.util.UUID
            
            val version = UUID.randomUUID()!!
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report safe call on nullable type`() {
        val code = """
            fun test(str: String?) {
                println(str?.length)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report safe call in combination with the elvis operator`() {
        val code = """
            fun test(str: String?) {
                println(str?.length ?: 0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
