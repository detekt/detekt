package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ImplicitDefaultLocaleSpec(private val env: KotlinCoreEnvironment) {
    private val subject = ImplicitDefaultLocale(Config.empty)

    @Nested
    inner class `ImplicitDefault rule` {

        @Test
        fun `reports String_format call with template but without explicit locale`() {
            val code = """
                fun x() {
                    String.format("%d", 1)
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report String_format call with explicit locale`() {
            val code = """
                import java.util.Locale
                fun x() {
                    String.format(Locale.US, "%d", 1)
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports String_toUpperCase() call without explicit locale`() {
            val code = """
                fun x() {
                    val s = "deadbeef"
                    s.toUpperCase()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report String_toUpperCase() call with explicit locale`() {
            val code = """
                import java.util.Locale
                fun x() {
                    val s = "deadbeef"
                    s.toUpperCase(Locale.US)
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports String_toLowerCase() call without explicit locale`() {
            val code = """
                fun x() {
                    val s = "deadbeef"
                    s.toLowerCase()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report String_toLowerCase() call with explicit locale`() {
            val code = """
                import java.util.Locale
                fun x() {
                    val s = "deadbeef"
                    s.toLowerCase(Locale.US)
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports nullable String_toUpperCase call without explicit locale`() {
            val code = """
                fun x() {
                    val s: String? = "deadbeef"
                    s?.toUpperCase()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports nullable String_toLowerCase call without explicit locale`() {
            val code = """
                fun x() {
                    val s: String? = "deadbeef"
                    s?.toLowerCase()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }
}
