package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryNotNullOperatorSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = UnnecessaryNotNullOperator(Config.empty)

    @Nested
    inner class `check unnecessary not null operators` {

        @Test
        fun `reports a simple not null operator usage`() {
            val code = """
                val a = 1
                val b = a!!
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(18 to 21)
        }

        @Test
        fun `reports a chained not null operator usage`() {
            val code = """
                val a = 1
                val b = a!!.plus(42)
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(18 to 21)
        }

        @Test
        fun `reports multiple chained not null operator usage`() {
            val code = """
                val a = 1
                val b = a!!.plus(42)!!
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(18 to 21, 18 to 32)
        }
    }

    @Nested
    inner class `check valid not null operators usage` {

        @Test
        fun `does not report a simple not null operator usage on nullable type`() {
            val code = """
                val a : Int? = 1
                val b = a!!
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
