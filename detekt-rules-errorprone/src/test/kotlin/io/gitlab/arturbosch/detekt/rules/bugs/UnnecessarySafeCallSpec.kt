package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessarySafeCallSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = UnnecessarySafeCall(Config.empty)

    @Nested
    inner class `check unnecessary safe operators` {

        @Test
        fun `reports a simple safe operator usage`() {
            val code = """
                fun test(s: String) {
                    val a = 1
                    val b = a?.toString()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(48 to 61)
        }

        @Test
        fun `reports a chained safe operator usage`() {
            val code = """
                fun test(s: String) {
                    val a = 1
                    val b = a?.plus(42)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(48 to 59)
        }

        @Test
        fun `reports multiple chained safe operator usage`() {
            val code = """
                fun test(s: String) {
                    val a = 1
                    val b = a?.plus(42)?.minus(24)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(48 to 59)
        }
    }

    @Nested
    inner class `check valid safe operators usage` {

        @Test
        fun `does not report a simple safe operator usage on nullable type`() {
            val code = """
                fun test(s: String) {
                    val a : Int? = 1
                    val b = a?.plus(42)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `check safe operator with non included types` {

        @Test
        fun `does not report safe calls with non specified types`() {
            val code = """
                import com.sample.function.from.outside
                
                fun test(s: String) {
                    val a = outside()
                    val b = a?.plus(42)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code, allowCompilationErrors = true)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report safe calls if nullable type is specified`() {
            val code = """
                import com.sample.function.from.outside
                
                fun test(s: String) {
                    val a : Int? = outside()
                    val b = a?.plus(42)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code, allowCompilationErrors = true)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports safe calls if non nullable type is specified`() {
            val code = """
                import com.sample.function.from.outside
                
                fun test(s: String) {
                    val a : Int = outside()
                    val b = a?.plus(42)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code, allowCompilationErrors = true)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(103 to 114)
        }
    }
}
