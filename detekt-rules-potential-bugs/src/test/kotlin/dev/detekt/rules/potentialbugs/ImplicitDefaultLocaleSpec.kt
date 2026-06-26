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

    @Test
    fun `does not report String_format with a hexadecimal conversion - issue 3821`() {
        val code = """
            fun x() {
                String.format("0x%08X", 255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report a format extension call with a hexadecimal conversion`() {
        val code = """
            fun x() {
                "0x%08x".format(255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report a format string with only locale-independent conversions`() {
        val code = """
            fun x() {
                "hex %x, octal %o, bool %b, char %c, str %s".format(255, 64, true, 'A', "y")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report a literal percent conversion`() {
        val code = """
            fun x() {
                "100%% done".format()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report a format string without conversions`() {
        val code = """
            fun x() {
                "plain text".format()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports a locale-dependent floating point conversion`() {
        val code = """
            fun x() {
                String.format("%.2f", 1.0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports a locale-dependent date time conversion`() {
        val code = """
            fun x() {
                "%tY".format(0L)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports when locale-independent and locale-dependent conversions are mixed`() {
        val code = """
            fun x() {
                "id 0x%X count %d".format(255, 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports when the format string is built with interpolation`() {
        val code = """
            fun x(prefix: String) {
                "${'$'}prefix %x".format(255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports when the format string is not a compile-time constant`() {
        val code = """
            fun x() {
                val pattern = "%x"
                pattern.format(255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }
}
