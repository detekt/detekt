package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseRequireNotNullSpec(val env: KotlinEnvironmentContainer) {
    val subject = UseRequireNotNull(Config.empty)

    @Test
    fun `reports 'require' calls with a non-null check`() {
        val code = """
            fun test(i: Int?) {
                require(i != null)
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports 'require' calls with a non-null check that has 'null' on the left side`() {
        val code = """
            fun test(i: Int?) {
                require(null != i)
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does not report a 'require' call without a non-null check`() {
        val code = """
            fun test(i: Int) {
                require(i > 0)
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }
}
