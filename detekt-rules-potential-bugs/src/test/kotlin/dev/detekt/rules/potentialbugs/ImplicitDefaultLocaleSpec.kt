package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ImplicitDefaultLocaleSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = ImplicitDefaultLocale(Config.empty)

    @Test
    fun `reports String_format call with template but without explicit locale`() {
        val code = """
            fun x() {
                String.format("%d", 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report String_format call with explicit locale`() {
        val code = """
            import java.util.Locale
            fun x() {
                String.format(Locale.US, "%d", 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report custom String_format call`() {
        val code = """
            fun String.Companion.format(format: String, value: Int) = format + value.toString()

            fun x() {
                String.format("%d", 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports format extension call with template but without explicit locale`() {
        val code = """
            fun x() {
                "%d".format(1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report format extension call with explicit locale`() {
        val code = """
            import java.util.Locale
            fun x() {
                "%d".format(Locale.US, 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for custom format extension call`() {
        val code = """
            fun String.format(value: Int): String {
                return value.toString()
            }
            fun x() {
                "%d".format(1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
