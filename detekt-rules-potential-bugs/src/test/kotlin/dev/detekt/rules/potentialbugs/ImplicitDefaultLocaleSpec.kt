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
    fun `does not report for locale-independent hexadecimal format specifier %x`() {
        val code = """
            fun x() {
                "0x%08x".format(255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent hexadecimal format specifier %X`() {
        val code = """
            fun x() {
                "0x%08X".format(255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent octal format specifier %o`() {
        val code = """
            fun x() {
                "%o".format(64)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent hexadecimal floating-point %a`() {
        val code = """
            fun x() {
                "%a".format(1.0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent boolean format specifier %b`() {
        val code = """
            fun x() {
                "%b".format(true)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent hash code format specifier %h`() {
        val code = """
            fun x() {
                "%h".format("test")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent string format specifier %s`() {
        val code = """
            fun x() {
                "%s".format("test")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent character format specifier %c`() {
        val code = """
            fun x() {
                "%c".format('A')
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent line separator %n`() {
        val code = """
            fun x() {
                "line1%nline2".format()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for literal percent %%`() {
        val code = """
            fun x() {
                "100%%".format()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for combined locale-independent specifiers`() {
        val code = """
            fun x() {
                "hex: 0x%08X, octal: %o, bool: %b".format(255, 64, true)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report String_format with locale-independent specifiers`() {
        val code = """
            fun x() {
                String.format("0x%08X", 255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports for locale-dependent decimal format specifier %d`() {
        val code = """
            fun x() {
                "%d".format(1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for locale-dependent floating-point format specifier %f`() {
        val code = """
            fun x() {
                "%f".format(1.5)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for locale-dependent scientific notation %e`() {
        val code = """
            fun x() {
                "%e".format(1000.0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for locale-dependent uppercase string %S`() {
        val code = """
            fun x() {
                "%S".format("test")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for locale-dependent uppercase character %C`() {
        val code = """
            fun x() {
                "%C".format('a')
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for mixed locale-independent and locale-dependent specifiers`() {
        val code = """
            fun x() {
                "hex: 0x%X, decimal: %d".format(255, 100)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for date-time format specifiers`() {
        val code = """
            import java.util.Date
            fun x() {
                "%tY".format(Date())
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }
}
