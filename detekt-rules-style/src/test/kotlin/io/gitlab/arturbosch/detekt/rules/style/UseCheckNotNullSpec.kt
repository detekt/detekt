package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseCheckNotNullSpec(val env: KotlinEnvironmentContainer) {
    val subject = UseCheckNotNull(Config.empty)

    @Test
    fun `reports 'check' calls with a non-null check`() {
        val code = """
            fun test(i: Int?) {
                check(i != null)
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports 'check' calls with a non-null check that has 'null' on the left side`() {
        val code = """
            fun test(i: Int?) {
                check(null != i)
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does not report a 'check' call without a non-null check`() {
        val code = """
            fun test(i: Int) {
                check(i > 0)
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }
}
